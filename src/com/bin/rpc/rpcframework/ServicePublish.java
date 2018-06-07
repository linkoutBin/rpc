package com.bin.rpc.rpcframework;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class ServicePublish {

    private static Logger log = Logger.getLogger(ServicePublish.class);

    private static boolean stop = true;

    private ServicePublish() {
    }

    public static void publish(Object service, int port) throws Exception {
        if (service == null)
            throw new IllegalArgumentException("发布服务不能为空");
        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("端口不合法" + port);
        try (ServerSocket server = new ServerSocket(port)) {
            while (stop) {
                try (Socket socket = server.accept()) {
                    log.info("重新接收socket请求");
                    new Thread(() -> dealSocket(socket, service)).start();
                }
            }
        }
    }

    private static void dealSocket(Socket socket, Object service){
        try{
            log.info("开始进入远程请求处理流程");
            if (socket.isInputShutdown()){
                log.info("inputstream closed");
            }else {
                log.info("获取input输入流");
                if(socket.isClosed()){
                    log.info("remote-socket is closed");
                }else {
                    log.info("start to get inputStream");
                    InputStream is = socket.getInputStream();
                    log.info("包装为ObjectInputStream-start");
                    ObjectInputStream input = new ObjectInputStream(is);
                    log.info("包装input流为ObjectInput流-end");
                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream output = new ObjectOutputStream(os);
                    log.info("获取output输出流");
                    String methodName = input.readUTF();
                    log.info("获取到远程请求方法名："+methodName);
                    Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                    Object[] arguments = (Object[]) input.readObject();
                    Method method = service.getClass().getMethod(methodName, parameterTypes);
                    log.info("===========开始生成代理结果=============");
                    Object result = method.invoke(service, arguments);
                    output.writeObject(result);
                }
            }
            log.info("start next loop");
        }catch (Exception e){
            log.info(e);
        }
    }

    public static void setStop(boolean stop) {
        ServicePublish.stop = stop;
    }
}
