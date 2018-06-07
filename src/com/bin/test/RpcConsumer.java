package com.bin.test;

import com.bin.rpc.rpcframework.ServiceCall;
import com.bin.rpc.rpcservice.HelloService;

public class RpcConsumer {
    public static void main(String[] args) throws Exception {
        Object o = ServiceCall.call(HelloService.class, "localhost", 8888);
        HelloService helloService = (HelloService) o;
        String result = helloService.sayHello("zhajk");
        System.out.println("get result:" + result);
    }
}
