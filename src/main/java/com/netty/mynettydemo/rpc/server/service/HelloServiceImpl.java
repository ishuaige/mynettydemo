package com.netty.mynettydemo.rpc.server.service;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String msg) {

        return "你好, " + msg;
    }
}