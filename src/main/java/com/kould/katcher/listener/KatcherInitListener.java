package com.kould.katcher.listener;

import com.google.gson.Gson;
import com.kould.katcher.adapter.UriActionHandlerAdapter;
import com.kould.katcher.annotation.Controller;
import com.kould.katcher.annotation.Mapping;
import com.kould.katcher.handler.HttpRequestActionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Katcher初始化器
 * 1、用于自动获取对应@Controller和@Mapping，并解析为Uri和UriActionHandlerAdapter装填如HttpRequestActionHandler的容器之中
 */
@Component
public class KatcherInitListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Gson gson = new Gson();

    private Logger logger = LoggerFactory.getLogger(KatcherInitListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent()==null){
            Map<String,Object> beans = event.getApplicationContext().getBeansWithAnnotation(Controller.class);
            for (Object controller : beans.values()) {
                Method[] methods = controller.getClass().getMethods();
                for (Method method : methods) {
                    Mapping mapping = method.getAnnotation(Mapping.class);
                    if (mapping != null) {
                        String parentUri = controller.getClass().getAnnotation(Controller.class).uri();
                        String childUri = mapping.uri();
                        String httpMethod = mapping.method().getValue();
                        HttpRequestActionHandler.CONTROLLER_MAP.put(httpMethod + parentUri + childUri
                                , new UriActionHandlerAdapter(controller,method));
                    }
                }
            }
        }
    }
}


