package com.kould.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kould.adapter.UriActionHandlerAdapter;
import com.kould.controller.TestController;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequestActionHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    Logger logger = LoggerFactory.getLogger(HttpRequestActionHandler.class);

    private static final Gson gson = new Gson();

    private static final Map<String, UriActionHandlerAdapter> CONTROLLER_MAP = new ConcurrentHashMap<>();


    static {
        try {
            CONTROLLER_MAP.put("/test",new UriActionHandlerAdapter(new TestController(), TestController.class.getMethod("test", String.class,String.class,String.class))) ;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        FullHttpResponse response ;
        Map<String, Object> params = null;
        if(fullHttpRequest.method() == HttpMethod.GET){
            params = getGetParamsFromChannel(fullHttpRequest);
        }else if(fullHttpRequest.method() == HttpMethod.POST){
            params = getPostParamsFromChannel(fullHttpRequest);
        } else {
            response = responseOk(HttpResponseStatus.BAD_REQUEST, null) ;
        }

        String fullUri = fullHttpRequest.uri();
        String uri = null;
        uri = getUri(fullUri);
        UriActionHandlerAdapter uriActionHandlerAdapter = CONTROLLER_MAP.get(uri);
        if (uriActionHandlerAdapter == null) {
            channelHandlerContext.fireChannelRead(fullHttpRequest.retain()) ;
        } else {
            Object data = uriActionHandlerAdapter.actionInvoke(params);
            ByteBuf buf = copiedBuffer(gson.toJson(data), CharsetUtil.UTF_8);
            response = responseOk(HttpResponseStatus.OK, buf);
            // 发送响应
            channelHandlerContext.writeAndFlush(response)
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }

    private String getUri(String fullUri) {
        String uri;
        int paramIndex = fullUri.indexOf("?");
        if (paramIndex > 0) {
            uri = fullUri.substring(0,paramIndex);
        } else {
            uri = fullUri;
        }
        return uri;
    }

    private ByteBuf copiedBuffer(String data, Charset utf8) {
        return Unpooled.wrappedBuffer(data.getBytes());
    }

    private Map<String, Object> getPostParamsFromChannel(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<>();
        if(fullHttpRequest.method() == HttpMethod.POST){
            // 处理post 请求
            String strContentType = fullHttpRequest.headers().get("Content-Type").trim();
            if(StringUtil.isNullOrEmpty(strContentType)){
                return null;
            }
            if(strContentType.contains("x-www-form-urlencoded")){
                params = getFormParams(fullHttpRequest);
            }else if(strContentType.contains("application/json")){
                params = getJSONParams(fullHttpRequest);
            }else {
                return null;
            }
        }
        return params;
    }

    private Map<String, Object> getJSONParams(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<>();

        ByteBuf content = fullHttpRequest.content();
        byte[] reqContent = new byte[content.readableBytes()];
        content.readBytes(reqContent);
        String strContent = null;
        strContent = new String(reqContent, StandardCharsets.UTF_8);
        return gson.fromJson(strContent, new TypeToken<List<Object>>() {}.getType());

    }

    private Map<String, Object> getFormParams(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<>();

        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), fullHttpRequest);
        List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();

        for (InterfaceHttpData data : postData) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                MemoryAttribute attribute = (MemoryAttribute) data;
                params.put(attribute.getName(), attribute.getValue());
            }
        }

        return params;
    }

    private Map<String, Object> getGetParamsFromChannel(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<>();
        if(fullHttpRequest.method() == HttpMethod.GET){
            QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.uri());
            Map<String, List<String>> paramList = decoder.parameters();

            for(Map.Entry<String, List<String>> entry : paramList.entrySet()){
                params.put(entry.getKey(),entry.getValue().get(0));
            }
            return params;
        }
        return params;
    }

    private FullHttpResponse responseOk(HttpResponseStatus status, ByteBuf buf) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                status,buf);
        if(buf != null){
            response.headers().set("Content-Type","text/plain;charset=UTF-8");
            response.headers().set("Content-Length",response.content().readableBytes());
        }
        return response;
    }
}
