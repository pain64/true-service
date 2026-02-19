package net.truej.service.low;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class MySocket extends Socket {
    private final IoUringEventLoop eventLoop;
    private short socketId;

    public MySocket(IoUringEventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    @Override public void connect(SocketAddress endpoint, int timeout) {
        var inetAddress = (InetSocketAddress) endpoint;
        var v4Address = (Inet4Address) inetAddress.getAddress();
        var port = inetAddress.getPort();

        try {
            var fd = LibC.socket(/* AF_INET */ 2, /* SOCK_STREAM */ 1, 0);
            LibC.setsockopt(fd, /* SOL_TCP    */ 6, /* TCP_NODELAY  */ 1, 1);
            LibC.connect(fd, v4Address.getAddress(), (short) port);
            socketId = eventLoop.socketAttach(fd);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override public InputStream getInputStream() {
        return new MyInputStream(eventLoop, socketId);
    }

    @Override public OutputStream getOutputStream() {
        return new MyOutputStream(eventLoop, socketId);
    }

    @Override public void close() throws IOException {
        eventLoop.socketDetach(socketId);
    }
}
