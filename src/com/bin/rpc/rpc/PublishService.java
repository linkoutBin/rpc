package com.bin.rpc.rpc;

import com.bin.rpc.rpcservice.HelloService;
import com.bin.rpc.rpcservice.ShowMessage;
import com.bin.rpc.rpcservice.impl.HelloServiceImpl;
import com.bin.rpc.rpcservice.impl.ShowMessageImpl;

import java.util.HashMap;
import java.util.Map;

public class PublishService {
    public static void main(String[] args) {
        int port = 8888;
        Map<Class<?>, Object> registerCenter = new HashMap<>();
        registerCenter.put(HelloService.class, new HelloServiceImpl());
        registerCenter.put(ShowMessage.class, new ShowMessageImpl());
        RemoteService.publish(registerCenter, port);
    }
}
