package com.kould.katcher.serialize.impl;

import com.kould.katcher.serialize.SerializeHandler;

public class DirectHandler implements SerializeHandler {

    private static final SerializeHandler directHandler = new DirectHandler();

    @Override
    public Object machining(Object result) {
        return result;
    }

    public static SerializeHandler getInstance() {
        return directHandler;
    }
}
