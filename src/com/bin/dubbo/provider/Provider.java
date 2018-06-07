package com.bin.dubbo.provider;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.bin.rpc.rpcservice.HelloService;
import com.bin.rpc.rpcservice.impl.HelloServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Provider {
    private static Logger log = LoggerFactory.getLogger(Provider.class);

    public static void main(String[] args) throws IOException {
        HelloService helloService = new HelloServiceImpl();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("service-provider");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("zookeeper://127.0.0.1:2181");

        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(12345);

        ServiceConfig<HelloService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setProtocol(protocolConfig);
        serviceConfig.setInterface(HelloService.class);
        serviceConfig.setRef(helloService);
        serviceConfig.setVersion("1.0.0");

        serviceConfig.export();

        log.info("【Dubbo 服务发布完成】");
        System.in.read();

    }
}
