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
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object result ;
        if (parameterNames != null) {
            Object[] args = new Object[parameterNames.length] ;
            for (int i = 0; i < parameterNames.length; i++) {
                Class<?> type = parameterTypes[i];
                args[i] = paramList.get(parameterNames[i]);
                if (args[i] == null && type.isPrimitive()) {
                    if (byte.class.equals(type)) {
                        args[i] = (byte) 0;
                    } else if (char.class.equals(type)) {
                        args[i] = (char) 0;
                    } else if (short.class.equals(type)) {
                        args[i] = (short) 0;
                    } else if (int.class.equals(type)) {
                        args[i] = 0;
                    } else if (long.class.equals(type)) {
                        args[i] = (long) 0;
                    } else if (float.class.equals(type)) {
                        args[i] = (float) 0;
                    } else if (double.class.equals(type)) {
                        args[i] = (double) 0;
                    } else {
                        throw new IllegalArgumentException("Primitive Param NoSuch"); //should be unreachable
                    }
                }
            }
            result = method.invoke(controller, args);
        } else {
            result = method.invoke(controller);
        }
        return handler.machining(result);
    }
}
