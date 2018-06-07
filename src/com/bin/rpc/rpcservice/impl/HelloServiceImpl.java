package com.bin.rpc.rpcservice.impl;

import com.bin.rpc.rpcservice.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hello "+ name;
    }
}
