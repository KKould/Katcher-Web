package com.kould.adapter;

import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class UriActionHandlerAdapter {

    DefaultParameterNameDiscoverer discover = new DefaultParameterNameDiscoverer();

    private final Object controller ;

    private final Method method ;

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
