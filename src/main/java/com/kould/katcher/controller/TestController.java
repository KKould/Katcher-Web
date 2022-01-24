package com.kould.katcher.controller;

import com.kould.katcher.annotation.Controller;
import com.kould.katcher.annotation.Mapping;
import com.kould.katcher.status.HttpMethod;

@Controller(uri = "/test")
public class TestController {

    @Mapping(uri = "/test", method = HttpMethod.GET)
    public String test(String args,String args1, String args2) {
        return args + args1 + args2;
    }
}
