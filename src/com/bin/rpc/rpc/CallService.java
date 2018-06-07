package com.bin.rpc.rpc;

import com.bin.rpc.rpcservice.HelloService;
import com.bin.rpc.rpcservice.ShowMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallService {
    private static Logger log = LoggerFactory.getLogger(CallService.class);

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8888;
        new Thread(() -> sayHello(host, port)).start();
        new Thread(() -> show(host, port)).start();
    }

    private static void sayHello(String host, int port) {
        try {
            HelloService helloService = RemoteService.call(HelloService.class, host, port);
            String result = helloService.sayHello("haah");
            log.info("result from remoteService helloservice:{}", result);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static void show(String host, int port) {
        try {
            ShowMessage showMessage = RemoteService.call(ShowMessage.class, host, port);
            String message = showMessage.showMessage("today news");
            log.info("result from remoteService showmessage:{}", message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
