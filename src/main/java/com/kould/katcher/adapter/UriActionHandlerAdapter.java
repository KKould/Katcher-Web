package com.kould.katcher.adapter;

import com.kould.katcher.serialize.SerializeHandler;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class UriActionHandlerAdapter {

    private final Object controller ;

    private final Method method ;

    private final SerializeHandler handler ;

    private static final DefaultParameterNameDiscoverer discover = new DefaultParameterNameDiscoverer();

    public UriActionHandlerAdapter(Object controller, Method method, SerializeHandler handler) {
        this.controller = controller;
        this.method = method;
        this.handler = handler;
    }

    public Object actionInvoke(Map<String, Object> paramList) throws InvocationTargetException, IllegalAccessException {
        String[] parameterNames = discover.getParameterNames(method);
        Object result ;
        if (parameterNames != null) {
            Object[] args = new Object[parameterNames.length] ;
            for (int i = 0; i < parameterNames.length; i++) {
                args[i] = paramList.get(parameterNames[i]);
            }
            result = method.invoke(controller, args);
        } else {
            result = method.invoke(controller);
        }
        return handler.machining(result);
    }
}
