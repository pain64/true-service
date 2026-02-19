import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.uring.IoUringChannelOption;
import io.netty.channel.uring.IoUringIoHandler;
import io.netty.channel.uring.IoUringServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.*;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;


public class TestNetty1 {

    private static final byte[] CONTENT =
        "HTTP/1.1 200 OK\r\nContent-Length: 12\r\nContent-Type: text/html\r\n\r\nHello World!"
            .getBytes(StandardCharsets.UTF_8);

    static void main() throws InterruptedException {

        for (var i = 0; i < 16; i++) {
            var boostrap1 = new ServerBootstrap();
            boostrap1
                .option(IoUringChannelOption.SO_REUSEADDR, true)
                .option(IoUringChannelOption.SO_REUSEPORT, true)
                .group(new MultiThreadIoEventLoopGroup(1, IoUringIoHandler.newFactory()))
                .channel(IoUringServerSocketChannel.class)
                //.handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override protected void initChannel(SocketChannel ch) {
                        ch
                            .pipeline()
                            .addLast(new ChannelInboundHandlerAdapter() {
                                @Override public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                    ctx.writeAndFlush(Unpooled.wrappedBuffer(CONTENT));
                                }
                            });
                    }
                });


            boostrap1.bind(new InetSocketAddress(8081)).sync();
        }

        new CountDownLatch(1).await();
    }
}
