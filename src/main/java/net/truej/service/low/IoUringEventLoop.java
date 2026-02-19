package net.truej.service.low;

import net.truej.service.low.LibC.URing;

import java.io.Closeable;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayDeque;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;

import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static net.truej.service.low.LibC.*;
import static net.truej.service.low.LibC.listen;

public class IoUringEventLoop implements Closeable {

    public static int MAX_CONNECTIONS = 768;
    static int MAX_HTTP_HANDLERS = 128;
    static int BUFFER_SIZE = 4096; // FIXME: make 4K ???

    private final URing ring;
    private final Arena allocator;
    private final MemorySegment readBuffers;
    private final MemorySegment writeBuffers;

    public final ArrayDeque<Short> socketIds = new ArrayDeque<>();
    //private final short[] alloc = new short[MAX_CONNECTIONS];
    //public  volatile int allocCurrent = 0;

    public final int[] socketIdToFd = new int[MAX_CONNECTIONS];
    private final Thread[] socketOpWakeup = new Thread[MAX_CONNECTIONS];
    private final int[] socketOpWakeupResult = new int[MAX_CONNECTIONS];

    private final Thread[] httpHandlerWakeupQueue = new Thread[MAX_HTTP_HANDLERS];
    private final short[] httpHandlerWakeupSocketIdQueue = new short[MAX_HTTP_HANDLERS];
    public volatile int httpHandlerWakeupQueueIndex = -1;

    private final short[] httpRequestSocketIdQueue = new short[MAX_CONNECTIONS];
    public volatile int httpRequestSocketIdQueueIndex = -1;

    private final int serverSocketFd;
    private final MemorySegment clientAddress;
    private final MemorySegment clientAddressLength;

    private int nSubmit = 0;
    private long entries = 0;

    public IoUringEventLoop() {
        try {
            serverSocketFd = socket(/* AF_INET */ 2, /* SOCK_STREAM */ 1, 0);

            setsockopt(serverSocketFd, /* SOL_SOCKET */ 1, /* SO_REUSEADDR */ 2, 1);
            setsockopt(serverSocketFd, /* SOL_SOCKET */ 1, /* SO_REUSEPORT */ 15, 1);
            setsockopt(serverSocketFd, /* SOL_TCP    */ 6, /* TCP_NODELAY  */ 1, 1);

            bind(serverSocketFd, new byte[]{127, 0, 0, 1}, (short) 7777);
            listen(serverSocketFd, 512);

            ring = io_uring_setup(2048);

            clientAddress = Arena.global().allocate(16 /* sizeof(struct sockaddr_in) */);
            clientAddressLength = Arena.global().allocate(8 /* sizeof long */);
            clientAddressLength.set(JAVA_LONG, 0, 16);

            ring.submitAccept(serverSocketFd, clientAddress, clientAddressLength, 0);
            nSubmit++;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        for (var i = 0; i < MAX_CONNECTIONS; i++) {
            socketIdToFd[i] = -1;
            //alloc[i] = (short) i;
            socketIds.addFirst((short) i);
        }

        allocator = Arena.ofConfined();
        readBuffers = allocator.allocate(BUFFER_SIZE * (long) MAX_CONNECTIONS, BUFFER_SIZE);
        writeBuffers = allocator.allocate(BUFFER_SIZE * (long) MAX_CONNECTIONS, BUFFER_SIZE);
    }

    // connection: fd -> serial dense index. now we can use array of buffers
    public short socketAttach(int fd) {
        var socketId = socketIds.removeLast();
        //var socketId = alloc[allocCurrent++];
        socketIdToFd[socketId] = fd;
        //System.out.println("attach: socketId = " + socketId + " fd = " + fd);
        return socketId;
    }

    public void socketDetach(short socketId) {
        //System.out.println("detach " + socketId);
//        if (socketIds.contains(socketId))
//            throw new RuntimeException("bad detach!");

        //alloc[--allocCurrent] = socketId;
        socketIds.addFirst(socketId);
        socketIdToFd[socketId] = -1;
    }

    private MemorySegment bufferForSocketId(MemorySegment buffers, short socketId) {
        return MemorySegment.ofAddress(
            buffers.address() + BUFFER_SIZE * (long) socketId
        ).reinterpret(BUFFER_SIZE);
    }

    public MemorySegment readBuffer(short socketId) {
        return bufferForSocketId(readBuffers, socketId);
    }

    public MemorySegment writeBuffer(short socketId) {
        return bufferForSocketId(writeBuffers, socketId);
    }

    private <T> T awaitSocketOp(
        short socketId, Runnable op, Function<Integer, T> onReady
    ) {
        socketOpWakeup[socketId] = Thread.currentThread();

        op.run();
        nSubmit++;

        while (socketOpWakeup[socketId] != null)
            LockSupport.park();

        var cqe = socketOpWakeupResult[socketId];
        socketOpWakeupResult[socketId] = 0;
        return onReady.apply(cqe);
    }

    public int awaitSocketRead(short socketId) {
        return awaitSocketOp(
            socketId,
            () -> ring.submitRead(
                socketIdToFd[socketId], readBuffer(socketId),
                BUFFER_SIZE, 0x03_00000000000000L | socketId
            ),
            ret -> {
                if (ret <= 0) /* 0 means END OF FILE ??? */ {
                    try {
                        throw new RuntimeException(LibC.strerror(-ret));
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                return ret;
            }
        );
    }

    public void awaitSocketWrite(short socketId, int length) {
        awaitSocketOp(
            socketId,
            () -> ring.submitWrite(
                socketIdToFd[socketId], writeBuffer(socketId),
                length, 0x03_00000000000000L | socketId
            ),
            ret -> {
                if (ret <= 0) /* 0 means END OF FILE ??? */ {
                    try {
                        throw new RuntimeException(LibC.strerror(-ret));
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                return null;
            }
        );
    }

    public short awaitNextHttpRequest() { // returns socketId
        var xx = 1;
        final MemorySegment cqe;
        if (httpRequestSocketIdQueueIndex == -1) {
            var idx = ++httpHandlerWakeupQueueIndex;
            httpHandlerWakeupQueue[idx] = Thread.currentThread();
            while (httpHandlerWakeupQueue[idx] != null)
                LockSupport.park();
            // System.out.println("awaiting fiber unparked");
            var socketId = httpHandlerWakeupSocketIdQueue[idx];
            httpHandlerWakeupSocketIdQueue[idx] = 0;

//            var userData = cqe.get(JAVA_LONG, 0);
//            var socketId = (short) (userData & 0x000000000000_FFFFL);
            return socketId;
        } else {
            var idx = httpRequestSocketIdQueueIndex--;
            var socketId = httpRequestSocketIdQueue[idx];
            httpRequestSocketIdQueue[idx] = 0;

//            var userData = cqe.get(JAVA_LONG, 0);
//            var socketId = (short) (userData & 0x000000000000_FFFFL);
            return socketId;
        }


    }

    public void sendHttpResponse(short socketId, int length) {
        ring.submitWrite(
            socketIdToFd[socketId], writeBuffer(socketId),
            length, 0x02_00000000000000L | socketId
        );
        nSubmit++; // FIXME: move nSubmit to URing class field
    }

    public void processAllEvents() {
        var head = ring.cqHead.get(JAVA_INT, 0);

        do {
            if (head == ring.cqTail.get(JAVA_INT, 0))
                break;

            var cqe = MemorySegment.ofAddress(
                ring.cqEntries.address() + (long) (head & ring.cqRingMask.get(JAVA_INT, 0)) * 16
            ).reinterpret(16);

            var userData = cqe.get(JAVA_LONG, 0);
            switch ((byte) ((userData & 0xFF_00000000000000L) >> 56)) {
                case 0 -> { // http accept finished

                    var clientFd = cqe.get(JAVA_INT, 8);
                    if (clientFd > 0) { // accept without error. FIXME: log ???
                        var socketId = socketAttach(clientFd);
                        //System.out.println(Thread.currentThread() + " connect as id: " + socketId + "to fd " + clientFd);
                        ring.submitRead(
                            socketIdToFd[socketId], readBuffer(socketId),
                            BUFFER_SIZE, 0x01_00000000000000L | socketId
                        );
                        nSubmit++;
                    }

                    ring.submitAccept(serverSocketFd, clientAddress, clientAddressLength, 0);
                    nSubmit++;
                }
                case 1 -> { // http read finished
                    var socketId = (short) (userData & 0x000000000000_FFFFL);
                    var ret = cqe.get(JAVA_INT, 8);
                    if (ret <= 0) {
                        //System.out.println("close by read " + socketId + " fd = " + socketIdToFd[socketId]);
                        try {
                            LibC.close(socketIdToFd[socketId]);
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        socketDetach(socketId);
                    } else {
                        //System.out.println("read success from " + socketId);
                        if (httpHandlerWakeupQueueIndex == -1)
                            httpRequestSocketIdQueue[++httpRequestSocketIdQueueIndex] = socketId;
                        else {
                            //System.out.println("unpark awaiting fiber");
                            var idx = httpHandlerWakeupQueueIndex--;
                            var threadToUnpark = httpHandlerWakeupQueue[idx];
                            httpHandlerWakeupQueue[idx] = null;
                            httpHandlerWakeupSocketIdQueue[idx] = socketId;
                            LockSupport.unpark(threadToUnpark);
                        }
                    }
                }
                case 2 -> { // http write finished
                    var socketId = (short) (userData & 0x000000000000_FFFFL);
                    var ret = cqe.get(JAVA_INT, 8);
                    if (ret <= 0) {
                        //System.out.println("close by write id = " + socketId + " fd = " + socketIdToFd[socketId]);
                        try {
                            LibC.close(socketIdToFd[socketId]);
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        socketDetach(socketId);
                    } else {
                        //System.out.println("write success to " + socketId);
                        ring.submitRead(
                            socketIdToFd[socketId], readBuffer(socketId), BUFFER_SIZE,
                            0x01_00000000000000L | socketId
                        );
                        nSubmit++;
                    }
                }
                case 3 -> { // socket op finished
                    var socketId = (short) (userData & 0x000000000000_FFFFL);
                    var threadToUnpark = socketOpWakeup[socketId];
                    socketOpWakeup[socketId] = null;
                    socketOpWakeupResult[socketId] = cqe.get(JAVA_INT, 8);
                    LockSupport.unpark(threadToUnpark);
                }
            }

            head++;
        } while (true);

        ring.cqHead.set(JAVA_INT, 0, head);

        try {
//            if (entries > 1_000_000_00) {
//                ring.enter(nSubmit, 1, 1 /* IORING_ENTER_GETEVENTS */);
//            } else {
                ring.enter(nSubmit, 0, 0);
//                entries++;
//            }

            nSubmit = 0;
//            if (ring.cqHead.get(JAVA_INT, 0) == ring.cqTail.get(JAVA_INT, 0)) {
//                LockSupport.parkNanos(100);
//            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void close() {
        allocator.close();
        ring.close();
    }
}