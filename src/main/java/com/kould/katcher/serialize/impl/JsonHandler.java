package com.kould.katcher.serialize.impl;

import com.google.gson.Gson;
import com.kould.katcher.serialize.SerializeHandler;

public class JsonHandler implements SerializeHandler {

    private static final Gson gson = new Gson();

    private static final SerializeHandler jsonHandler = new JsonHandler();

    @Override
    public Object machining(Object result) {
        return gson.toJson(result);
    }

    public static SerializeHandler getInstance() {
        return jsonHandler;
    }
}
