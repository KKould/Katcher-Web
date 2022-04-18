package com.kould.katcher.adapter;

import com.kould.katcher.serialize.SerializeHandler;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Field;
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

    public Object actionInvoke(Map<String, Object> paramMap) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        String[] parameterNames = discover.getParameterNames(method);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object result ;
        if (parameterNames != null) {
            Object[] args = new Object[parameterNames.length] ;
            for (int i = 0; i < parameterNames.length; i++) {
                Class<?> type = parameterTypes[i];
                args[i] = paramMap.get(parameterNames[i]);
                if (type.isPrimitive()) {
                    args[i] = str2Primitive(args[i], type);
                } else {
                    for (Field field : type.getDeclaredFields()) {
                        Object fieldVal = paramMap.get(field.getName());
                        if (fieldVal != null) {
                            if (args[i] == null) {
                                args[i] = type.newInstance();
                            }
                            Class<?> fieldType = field.getType();
                            field.setAccessible(true);
                            field.set(args[i], str2Primitive(fieldVal,fieldType));
                        }
                    }
                }
            }
            result = method.invoke(controller, args);
        } else {
            result = method.invoke(controller);
        }
        return handler.machining(result);
    }

    private Object str2Primitive(Object arg, Class<?> type) {
        if (arg == null) {
            if (byte.class.equals(type)) {
                return (byte) 0;
            } else if (char.class.equals(type)) {
                return (char) 0;
            } else if (short.class.equals(type)) {
                return (short) 0;
            } else if (int.class.equals(type)) {
                return 0;
            } else if (long.class.equals(type)) {
                return (long) 0;
            } else if (float.class.equals(type)) {
                return (float) 0;
            } else if (double.class.equals(type)) {
                return (double) 0;
            } else {
                throw new IllegalArgumentException("Primitive Param NoSuch"); //should be unreachable
            }
        } else if (arg instanceof String){
            if (byte.class.equals(type)) {
                return Byte.parseByte((String) arg);
            } else if (char.class.equals(type)) {
                return ((String) arg).charAt(0);
            } else if (short.class.equals(type)) {
                return Short.parseShort((String) arg);
            } else if (int.class.equals(type)) {
                return Integer.parseInt((String) arg);
            } else if (long.class.equals(type)) {
                return Long.parseLong((String) arg);
            } else if (float.class.equals(type)) {
                return Float.parseFloat((String) arg);
            } else if (double.class.equals(type)) {
                return Double.parseDouble((String) arg);
            } else {
                return arg;
            }
        } else {
            return null;
        }
    }
}
