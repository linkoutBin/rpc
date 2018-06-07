package com.bin.rpc.rpcframework;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class ServiceCall {

    private static Logger log = Logger.getLogger(ServiceCall.class);

    public static <T> T call(final Class<T> interfaceClass, String host, int port) throws Exception {
        log.info("进入远程调用流程");
        if (interfaceClass == null)
            throw new IllegalArgumentException("调用接口不能为空");
        if (StringUtils.isBlank(host))
            throw new IllegalArgumentException("服务方主机地址不能为空");
        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("端口不合法" + port);
        log.info("开始生成代理结果");
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new CallHandler(host, port));
    }

    static class CallHandler implements InvocationHandler {
        String host;
        int port;

        public CallHandler(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            log.info("开始远程调用,方法名：" + methodName);
            try(Socket socket = new Socket(host, port)) {
                try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                    outputStream.writeUTF(methodName);
                    log.info("method name set");
                    Thread.sleep(5000);
                    outputStream.flush();
                    if(socket.isClosed()){
                        log.info("socket is closed");
                    }else {
                        log.info("socket is still connected");
                        Thread.sleep(5000);
                    }
                    /*Class<?>[] classes = method.getParameterTypes();
                    log.info("get method parametertypes :"+ Arrays.toString(classes));
                    outputStream.writeObject(classes);
                    log.info("method parameterType set");
                    outputStream.writeObject(args);
                    log.info("method parameter set");*/
                    //outputStream.flush();
                    /*try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                        log.info("进入输入流处理");
                        Object result = inputStream.readObject();
                        if (result instanceof Throwable) {
                            log.error("远程调用返回异常");
                            throw (Exception) result;
                        }
                        return result;
                    }catch (Exception e2){
                        log.error("inputStream 流异常");
                        throw e2.getCause();
                    }*/
                }catch (Exception e1){
                    log.error("outputStream 流异常");
                    throw e1.getCause();
                }
                return null;
            } catch (Exception e) {
                log.error("socket 连接异常");
                throw e.getCause();
            }
        }
    }


}
