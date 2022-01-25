package com.kould.katcher.status;

import com.kould.katcher.serialize.SerializeHandler;
import com.kould.katcher.serialize.impl.DirectHandler;
import com.kould.katcher.serialize.impl.JsonHandler;

public enum  ReturnType {

    JSON(JsonHandler.getInstance()),
    DIRECT(DirectHandler.getInstance());
//    ,PAGE(),DIRECT();

    private final SerializeHandler serializeHandler ;

    public SerializeHandler getSerializeHandler() {
        return serializeHandler;
    }

    ReturnType(SerializeHandler serializeHandler) {
        this.serializeHandler = serializeHandler;
    }
}
