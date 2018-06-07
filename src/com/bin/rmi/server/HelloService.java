package com.bin.rmi.server;

import com.bin.rmi.remote.Hello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class HelloService implements Hello {
    private static Logger log = LoggerFactory.getLogger(HelloService.class);

    public HelloService() {
    }

    @Override
    public String sayHello(String name) {
        return "Hello "+name;
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(8888);
            Hello hello = (Hello) UnicastRemoteObject.exportObject(new HelloService(),8887);
            Hello hello1 = (Hello) UnicastRemoteObject.exportObject(new HelloService(),8886);
            log.info("export service done");
            Registry registry = LocateRegistry.getRegistry(8888);
            log.info("get register done");
            registry.bind("Hello",hello);
            registry.bind("Hello1",hello1);

            log.info("Server Ready");
            //Thread.sleep(10000);
        }catch (Exception e){
            log.error("Server Exception:"+e.getMessage());
        }
    }

}
