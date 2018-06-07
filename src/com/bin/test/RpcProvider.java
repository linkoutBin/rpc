package com.bin.test;

import com.bin.rpc.rpcframework.ServicePublish;
import com.bin.rpc.rpcservice.HelloService;
import com.bin.rpc.rpcservice.impl.HelloServiceImpl;

public class RpcProvider {
    public static void main(String[] args) throws Exception{
        HelloService helloService = new HelloServiceImpl();
        ServicePublish.publish(helloService,8888);
    }
}
