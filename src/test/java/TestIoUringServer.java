import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import static java.lang.foreign.MemorySegment.NULL;
import static java.lang.foreign.ValueLayout.*;
import static net.truej.service.low.LibC.*;

public class TestIoUringServer {
    // --add-opens java.base/java.lang=ALL-UNNAMED


    //@formatter:off
    //@formatter:on

    // 1. runner pool - custom single-threaded FJP & virtual threads - done
    // 2. io_uring layer - init & advance. pull mode. Refactor server at first
    // 3. execution state machine mode. park / unpark

    // how fiber must work
    // accept -> read -> write -|
    //          <---------------
    // each coroutine makes the same
    // handler function - state machine
    // accept -> connection list
    //

    static int MAX_CONNECTIONS = 1024;
    static int BUFFER_SIZE = 4096;

    static void submit(URing ring, Consumer<MemorySegment> sqeConsumer) {
        var tail = ring.sqTail.get(JAVA_INT, 0);
        var index = tail & ring.sqRingMask.get(JAVA_INT, 0);

        sqeConsumer.accept(
            MemorySegment.ofAddress(
                ring.sqEntries.address() + (long) index * 64
            ).reinterpret(64)
        );

        ring.sqArray.setAtIndex(JAVA_INT, index, index);
        ring.sqTail.set(JAVA_INT, 0, tail + 1);
    }

    static void submitAccept(
        URing ring, int socketFd,
        MemorySegment clientAddress, MemorySegment clientAddressLength
    ) {
        submit(ring, sqe -> {
            sqe.set(JAVA_BYTE, 0, (byte) 13 /* op = IORING_OP_ACCEPT */);
            sqe.set(JAVA_BYTE, 1, (byte) 0 /* flags */);
            sqe.set(JAVA_INT, 4, socketFd /* fd */);
            sqe.set(JAVA_LONG, 8, clientAddressLength.address() /* off */);
            sqe.set(JAVA_LONG, 16, clientAddress.address() /* addr */);
            sqe.set(JAVA_INT, 24, 0 /* len */);
            sqe.set(JAVA_LONG, 32, connectionInfoOf(0, State.ACCEPT, (short) 0) /* user_data */);
        });
    }

    static void submitRead(URing ring, MemorySegment buffers, int clientFd, short bufferNumber) {
        submit(ring, sqe -> {
            sqe.set(JAVA_BYTE, 0, (byte) 27 /* op = IORING_OP_RECV */);
            sqe.set(JAVA_BYTE, 1, (byte) 0 /* flags */);
            sqe.set(JAVA_INT, 4, clientFd /* fd */);
            sqe.set(JAVA_LONG, 8, 0 /* off */);
            sqe.set(
                JAVA_LONG, 16, buffers.address() + (long) BUFFER_SIZE * bufferNumber /* addr */
            );
            sqe.set(JAVA_INT, 24, BUFFER_SIZE /* len */);
            sqe.set(JAVA_LONG, 32,
                connectionInfoOf(
                    clientFd, State.READ, bufferNumber
                ) /* user_data */
            );
        });
    }

    static MemorySegment HELLO_RESPONSE = Arena.global().allocateFrom(
        "HTTP/1.1 200 OK\r\nContent-Length: 12\r\nContent-Type: text/html\r\n\r\nHello World!"
    );

    static MemorySegment HELLO_REQUEST_LINE = Arena.global().allocate(16, 16);
    static {
        var bytes = "GET / HTTP/1.1\r\n".getBytes(StandardCharsets.UTF_8);
        for (var i = 0; i < bytes.length; i++)
            HELLO_REQUEST_LINE.set(JAVA_BYTE, i, bytes[i]);
    }

    static void submitWrite(URing ring, MemorySegment buffers, int clientFd, short bufferNumber) {

        MemorySegment.copy(
            HELLO_RESPONSE, 0, buffers,
            (long) BUFFER_SIZE * bufferNumber, HELLO_RESPONSE.byteSize() - 1
        );

        submit(ring, sqe -> {
            sqe.set(JAVA_BYTE, 0, (byte) 26 /* op = IORING_OP_SEND */);
            sqe.set(JAVA_BYTE, 1, (byte) 0 /* flags */);
            sqe.set(JAVA_INT, 4, clientFd /* fd */);
            sqe.set(JAVA_LONG, 8, 0 /* off */);
            sqe.set(
                JAVA_LONG, 16, buffers.address() + (long) BUFFER_SIZE * bufferNumber /* addr */
            );
            sqe.set(JAVA_INT, 24, (int) HELLO_RESPONSE.byteSize() - 1 /* len */);
            sqe.set(JAVA_LONG, 32,
                connectionInfoOf(
                    clientFd, State.WRITE, bufferNumber
                ) /* user_data */
            );
        });
    }


    enum State {ACCEPT, READ, WRITE}

    static long connectionInfoOf(int fd, State state, short bufferNumber) {
        return (long) fd << 32 |
               (long) state.ordinal() << 16 |
               (long) bufferNumber;
    }

    static int fdIn(long connectionInfo) {
        return (int) ((connectionInfo & 0xffffffff00000000L) >> 32);
    }

    static State stateIn(long connectionInfo) {
        return switch ((short) ((connectionInfo & 0x00000000ffff0000L) >> 16)) {
            case 0 -> State.ACCEPT;
            case 1 -> State.READ;
            case 2 -> State.WRITE;
            default -> throw new RuntimeException("unreachable");
        };
    }

    static short bufferNumberIn(long connectionInfo) {
        return (short) (connectionInfo & 0x000000000000ffffL);
    }

    static void workerF() throws Throwable {

        var alloc = new short[MAX_CONNECTIONS];
        for (int i = 0; i < alloc.length; i++)
            alloc[i] = (short) i;
        var allocCurrent = 0;

        var buffers = mmap(
            NULL, (long) MAX_CONNECTIONS * BUFFER_SIZE,
            0x1 /* PROT_READ */ | 0x2 /* PROT_WRITE */,
            0x1 /* MAP_SHARED */ | 0x20 /* MAP_ANONYMOUS */,
            -1, 0
        ).reinterpret((long) MAX_CONNECTIONS * BUFFER_SIZE);

        var socketFd = socket(/* AF_INET */ 2, /* SOCK_STREAM */ 1, 0);

        setsockopt(socketFd, /* SOL_SOCKET */ 1, /* SO_REUSEADDR */ 2, 1);
        setsockopt(socketFd, /* SOL_SOCKET */ 1, /* SO_REUSEPORT */ 15, 1);
        setsockopt(socketFd, /* SOL_TCP    */ 6, /* TCP_NODELAY  */ 1, 1);

        bind(socketFd, new byte[]{127, 0, 0, 1}, (short) 7777);
        listen(socketFd, 512);

        var ring = io_uring_setup(2048);

        var clientAddress = Arena.global().allocate(16 /* sizeof(struct sockaddr_in) */);
        var clientAddressLength = Arena.global().allocate(8 /* sizeof long */);
        clientAddressLength.set(JAVA_LONG, 0, 16);

        submitAccept(ring, socketFd, clientAddress, clientAddressLength);
        var submissionCount = 1;

        while (true) {
            io_uring_enter(ring.fd, submissionCount, 1, 1 /* IORING_ENTER_GETEVENTS */);
            submissionCount = 0;

            var head = ring.cqHead.get(JAVA_INT, 0);
            do {
                if (head == ring.cqTail.get(JAVA_INT, 0))
                    break;

                var cqe = MemorySegment.ofAddress(
                    ring.cqEntries.address() + (long) (head & ring.cqRingMask.get(JAVA_INT, 0)) * 16
                ).reinterpret(16);

                var connectionInfo = cqe.get(JAVA_LONG, 0);
                switch (stateIn(connectionInfo)) {
                    case ACCEPT -> {
                        var clientFd = cqe.get(JAVA_INT, 8);
                        if (clientFd > 0) { // accept without error. FIXME: log ???
                            submitRead(ring, buffers, clientFd, alloc[allocCurrent++]);
                            submissionCount++;
                        }

                        submitAccept(ring, socketFd, clientAddress, clientAddressLength);
                        submissionCount++;
                    }
                    case READ -> {
                        var ret = cqe.get(JAVA_INT, 8);
                        if (ret <= 0) { /* 0 means END OF FILE ??? */
                            alloc[--allocCurrent] = bufferNumberIn(connectionInfo);
                            // TODO: close fd
                        } else {


                            var xx = 1;

                            if (
                                MemorySegment.mismatch(
                                    buffers, (long) BUFFER_SIZE * bufferNumberIn(connectionInfo), (long) BUFFER_SIZE * (bufferNumberIn(connectionInfo)) + HELLO_REQUEST_LINE.byteSize(),
                                    HELLO_REQUEST_LINE, 0, HELLO_REQUEST_LINE.byteSize()
                                ) != -1
                            )
                                throw new RuntimeException("NOT FOUND");

                            submitWrite(
                                ring, buffers, fdIn(connectionInfo),
                                bufferNumberIn(connectionInfo)
                            );
                            submissionCount++;
                        }
                    }
                    case WRITE -> {
                        var ret = cqe.get(JAVA_INT, 8);
                        if (ret < 0) {
                            alloc[--allocCurrent] = bufferNumberIn(connectionInfo);
                            // TODO: close fd
                        } else {
                            submitRead(
                                ring, buffers, fdIn(connectionInfo),
                                bufferNumberIn(connectionInfo)
                            );
                            submissionCount++;
                        }
                    }
                }

                head++;
            } while (true);

            ring.cqHead.set(JAVA_INT, 0, head);
        }
    }

    static int N_THREADS = 16;

    static void main() throws Throwable {
        var threads = new Thread[N_THREADS];
        for (var i = 0; i < N_THREADS; i++) {
            Class<?> cl1 = null;
            try {
                cl1 = Class.forName("java.lang.VirtualThread");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Method m1 = null;
            try {
                m1 = cl1.getDeclaredMethod("createDefaultScheduler");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            m1.setAccessible(true);

            System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
            System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");

            ForkJoinPool fjp = null;
            try {
                fjp = (ForkJoinPool) m1.invoke(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }

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
                threadBuilder = (Thread.Builder.OfVirtual) cons.newInstance(fjp);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            threadBuilder = threadBuilder.inheritInheritableThreadLocals(true);

            threads[i] = threadBuilder.start(() -> {
                try {
                    workerF();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
            // threads[i].start();
        }

        for (var i = 0; i < N_THREADS; i++)
            threads[i].join();
    }
}
