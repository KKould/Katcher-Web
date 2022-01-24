package com.kould.katcher.status;

/**
 * 用于标记Mapping的HttpMethod
 */
public enum HttpMethod {

    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

    private String value ;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    HttpMethod(String value) {
        this.value = value;
    }
}
