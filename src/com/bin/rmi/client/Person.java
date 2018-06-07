package com.bin.rmi.client;

import com.bin.rmi.remote.Hello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Person {
    private static Logger log = LoggerFactory.getLogger(Person.class);

    public Person() {
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(8888);
            log.info("get register done");
            Hello hello = (Hello) registry.lookup("Hello");
            log.info("get service from registry done");
            String hresponse = hello.sayHello("Joson");
            hello = (Hello) registry.lookup("Hello1");
            log.info("receive hmsg:{}", hresponse);
            log.info("receive h1msg:{}",hello.sayHello("aaaaa"));
        } catch (Exception e) {
            log.error("Client Exception:{}", e.getMessage());
            e.printStackTrace();
        }
    }

}
