import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.uring.IoUringChannelOption;
import io.netty.channel.uring.IoUringIoHandler;
import io.netty.channel.uring.IoUringServerSocketChannel;
import io.netty.channel.uring.IoUringSocketChannel;
import io.netty.handler.codec.http.*;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.*;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;


public class TestNetty2 {
    private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!' };

    static void main() throws InterruptedException {
        for (var i = 0; i < 16; i++) {
            var boostrap1 = new ServerBootstrap();
            boostrap1
                .option(IoUringChannelOption.SO_REUSEADDR, true)
                .option(IoUringChannelOption.SO_REUSEPORT, true)
                .group(new MultiThreadIoEventLoopGroup(1, IoUringIoHandler.newFactory()))
                .channel(IoUringServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override protected void initChannel(SocketChannel ch) {
                        ch
                            .pipeline()
                            .addLast(new HttpServerCodec())
                            .addLast(new HttpObjectAggregator(8192, true))
                            .addLast(new SimpleChannelInboundHandler<HttpObject>() {

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) {
                                    ctx.flush();
                                }

                                @Override protected void channelRead0(
                                    ChannelHandlerContext ctx, HttpObject msg
                                ) throws Exception {
                                    if (msg instanceof HttpRequest req) {
                                        var keepAlive = HttpUtil.isKeepAlive(req);
                                        var response = new DefaultFullHttpResponse(
                                            req.protocolVersion(), OK, Unpooled.wrappedBuffer(CONTENT)
                                        );
                                        response.headers()
                                            .set(CONTENT_TYPE, TEXT_PLAIN)
                                            .setInt(CONTENT_LENGTH, response.content().readableBytes());

                                        if (keepAlive) {
                                            if (!req.protocolVersion().isKeepAliveDefault())
                                                response.headers().set(CONNECTION, KEEP_ALIVE);
                                        } else
                                            // Tell the client we're going to close the connection.
                                            response.headers().set(CONNECTION, CLOSE);

                                        var f = ctx.write(response);

                                        if (!keepAlive)
                                            f.addListener(ChannelFutureListener.CLOSE);
                                    }
                                }
                            });
                    }
                });


            boostrap1.bind(new InetSocketAddress(8081)).sync();
        }

        new CountDownLatch(1).await();
    }
}
