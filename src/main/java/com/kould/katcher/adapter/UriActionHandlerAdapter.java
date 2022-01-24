package com.kould.katcher.adapter;

import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class UriActionHandlerAdapter {

    private final Object controller ;

    private final Method method ;

    private static final DefaultParameterNameDiscoverer discover = new DefaultParameterNameDiscoverer();

    public UriActionHandlerAdapter(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Object actionInvoke(Map<String, Object> paramList) throws InvocationTargetException, IllegalAccessException {
        String[] parameterNames = discover.getParameterNames(method);
        if (parameterNames != null) {
            Object[] args = new Object[parameterNames.length] ;
            for (int i = 0; i < parameterNames.length; i++) {
                args[i] = paramList.get(parameterNames[i]);
            }
            return method.invoke(controller, args);
        } else {
            return method.invoke(controller) ;
        }
    }
}
