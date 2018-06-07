package com.bin.rpc.rpc;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class RemoteService {
    private static Logger log = LoggerFactory.getLogger(RemoteService.class);

    private static boolean stop = true;

    public static void publish(Map<Class<?>, Object> service, int port) {
        try (ServerSocket server = new ServerSocket(port)) {
            while (stop) {
                log.info("start to accept request.....");
                Socket socket = server.accept();
                new Thread(() -> handleRequest(socket, service)).start();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static void handleRequest(Socket socket, Map<Class<?>, Object> service) {
        try {
            if (socket.isClosed()) {
                log.error("socket is closed");
            } else {
                log.debug("accepted a socket,remote add:{}", socket.getRemoteSocketAddress().toString());
                if (socket.isInputShutdown()) {
                    log.error("remote inputstream is shutdown");
                } else {
                    log.info("start to get inputstream");
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    log.info("start to read remote message:{}", ois.available());
                    String methodName = ois.readUTF();
                    Class<?>[] parameterTypes = (Class<?>[]) ois.readObject();
                    Object[] arguments = (Object[]) ois.readObject();
                    Class<?> interfaceClass = (Class<?>) ois.readObject();
                    log.info("receive from remote :InterfaceClass-{},ParamsTypes-{},method-{},arguments-{},", interfaceClass, parameterTypes, methodName, arguments);
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    log.info("+++++++++++++++开始远端请求实现类++++++++++++++++++++");
                    Method method = service.get(interfaceClass).getClass().getMethod(methodName, parameterTypes);
                    Object result = method.invoke(service.get(interfaceClass), arguments);
                    oos.writeObject(result);
                    Thread.sleep(5000);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    public static <T> T call(final Class<T> interfaceClass, String host, int port) throws Exception {
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
            try (Socket socket = new Socket(host, port)) {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeUTF(method.getName());
                Class<?>[] parameterTypes = method.getParameterTypes();
                oos.writeObject(parameterTypes);
                oos.writeObject(args);
                Class<?> interfaceClass = method.getDeclaringClass();
                oos.writeObject(interfaceClass);
                log.info("发送完成,开始接收响应");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object result = ois.readObject();
                if (result instanceof Throwable) {
                    log.error("远程响应结果为异常");
                    throw (Throwable) result;
                } else {
                    try(FileOutputStream fileOutputStream = new FileOutputStream("/Users/xingshulin/ownCode/webmagicN/class.txt")){
                        byte[] clazz = ProxyGenerator.generateProxyClass("$proxy4",new Class[]{proxy.getClass()});
                        fileOutputStream.write(clazz);
                        fileOutputStream.flush();
                    }

                    return result;
                }
            }

        }
    }

    public static void setStop(boolean stop) {
        RemoteService.stop = stop;
    }


    public static void main(String[] args) throws Exception{

    }

}
