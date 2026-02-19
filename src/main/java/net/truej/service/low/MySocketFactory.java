package net.truej.service.low;

import javax.net.SocketFactory;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class MySocketFactory extends SocketFactory  {
    public static final ConcurrentHashMap<String, IoUringEventLoop> loops =
        new ConcurrentHashMap<>();
    private final IoUringEventLoop instance;

    public MySocketFactory(String arg) {
        instance = loops.get(arg);
    }

    @Override public Socket createSocket() {
        System.out.println(instance);
        return new MySocket(instance);
    }

    @Override public Socket createSocket(String s, int i) {
        throw new RuntimeException("not implemented");
    }
    @Override public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) {
        throw new RuntimeException("not implemented");
    }
    @Override public Socket createSocket(InetAddress inetAddress, int i) {
        throw new RuntimeException("not implemented");
    }
    @Override public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) {
        throw new RuntimeException("not implemented");
    }
}
