import net.truej.service.low.IoUringEventLoop;
import net.truej.service.low.MySocketFactory;
import org.jctools.queues.atomic.MpscAtomicArrayQueue;
import org.jetbrains.annotations.NotNull;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;

public class HttpPgFullOpt {

    static MemorySegment HELLO_RESPONSE = Arena.global().allocateFrom(
        "HTTP/1.1 200 OK\r\nContent-Length: 12\r\nContent-Type: text/html\r\n\r\nHello World!"
    );

    static MemorySegment HELLO_REQUEST_LINE = Arena.global().allocate(16, 16);
    static {
        var bytes = "GET / HTTP/1.1\r\n".getBytes(StandardCharsets.UTF_8);
        for (var i = 0; i < bytes.length; i++)
            HELLO_REQUEST_LINE.set(JAVA_BYTE, i, bytes[i]);
    }

    static class MyExecutor implements Executor {
        private final MpscAtomicArrayQueue<Runnable> queue =
            new MpscAtomicArrayQueue<>(128);

        public MyExecutor(IoUringEventLoop eventLoop) {
            new Thread(() -> {
                while (true) {
                    var task = queue.poll();
                    if (task != null) task.run();
                    else {
                        // we have no tasks
                        eventLoop.processAllEvents();
                    }
                }
            }).start();
        }

        @Override public void execute(@NotNull Runnable runnable) {
            while (!queue.offer(runnable)) ;
        }
    }

    static int N_SERVERS = 11;
    static int N_IO = 8;


    static void main() throws InterruptedException, SQLException {
        var lock = new ReentrantLock();

        for (var s = 0; s < N_SERVERS; s++) {
            var ss = s;
            var eventLoop = new IoUringEventLoop();

            Class<?> cl = null;
            try {
                cl = Class.forName("java.lang.ThreadBuilders$VirtualThreadBuilder");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            var cons = cl.getDeclaredConstructors()[1];
            cons.setAccessible(true);
            Thread.Builder.OfVirtual threadBuilder = null;
            try {
                threadBuilder = (Thread.Builder.OfVirtual) cons.newInstance(new MyExecutor(eventLoop));
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }


            threadBuilder = threadBuilder
                .inheritInheritableThreadLocals(true)
                .uncaughtExceptionHandler((t, e) -> {
                    e.printStackTrace();
                    System.exit(1);
                });


            // Внутри event loop делать bind ???
            // Создавать сервер ???


            for (var i = 0; i < N_IO; i++) {
                var ii = i;
                threadBuilder.start(() -> {
                    MySocketFactory.loops.put(ss + "_" + ii, eventLoop);
                    Connection cn;
                    try {
                        //lock.lock();
                        cn = DriverManager.getConnection(
                            "jdbc:postgresql://localhost:5432/petclinic?binaryTransfer=false" +
                            "&sslmode=disable&socketFactory=net.truej.service.low.MySocketFactory" +
                            "&socketFactoryArg=" + ss + "_" + ii,
                            "sa", "1234"
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } finally {
                        //lock.unlock();
                    }

                    var xx = 1;

                    while (true) {
                        // endpoint function
                        var socketId = eventLoop.awaitNextHttpRequest();
                        var readBuffer = eventLoop.readBuffer(socketId);
                        //System.out.println("enter handler");

                        if (
                            MemorySegment.mismatch(
                                readBuffer, 0, HELLO_REQUEST_LINE.byteSize(),
                                HELLO_REQUEST_LINE, 0, HELLO_REQUEST_LINE.byteSize()
                            ) != -1
                        )
                            throw new RuntimeException("NOT FOUND");

                        try (var stmt = cn.prepareStatement("select 1")) {
                            try(var rs = stmt.executeQuery()) {
                                rs.next();
                                rs.getInt(1);
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        var writeBuffer = eventLoop.writeBuffer(socketId);
                        MemorySegment.copy(
                            HELLO_RESPONSE, 0,
                            writeBuffer, 0, HELLO_RESPONSE.byteSize() - 1
                        );

                        eventLoop.sendHttpResponse(socketId, (int) HELLO_RESPONSE.byteSize() - 1);
                    }
                });
            }
        }

        new Thread(() -> {
            while (true) {
                MySocketFactory.loops.values().stream().distinct().forEach(l -> {
                    System.out.println("loop connections: " + (l.MAX_CONNECTIONS - l.socketIds.size())

                    );
                    System.out.println("loop http wakeup queue: " + (l.httpHandlerWakeupQueueIndex));
                    System.out.println("loop http request queue: " + (l.httpRequestSocketIdQueueIndex));
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {


                }
            }
        }).start();

        Thread.sleep(1000000000);
    }
}

