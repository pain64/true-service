import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

import static java.lang.foreign.MemorySegment.NULL;
import static java.lang.foreign.ValueLayout.*;
import static net.truej.service.low.LibC.*;

public class TestIoUringServerWithPg2 {


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

        var tail = ring.sqTail.get(JAVA_INT, 0);
        var index = tail & ring.sqRingMask.get(JAVA_INT, 0);

//        var sqe = MemorySegment.ofAddress(
//            ring.sqEntries.address() + (long) index * 64
//        ).reinterpret(64);

        var base = (long) index * 64;


        ring.sqEntries.set(JAVA_BYTE, base + 0, (byte) 27 /* op = IORING_OP_RECV */);
        ring.sqEntries.set(JAVA_BYTE, base + 1, (byte) 0 /* flags */);
        ring.sqEntries.set(JAVA_INT, base + 4, clientFd /* fd */);
        ring.sqEntries.set(JAVA_LONG, base + 8, 0 /* off */);
        ring.sqEntries.set(
            JAVA_LONG, base + 16, buffers.address() + (long) BUFFER_SIZE * bufferNumber /* addr */
        );
        ring.sqEntries.set(JAVA_INT, base + 24, BUFFER_SIZE /* len */);
        ring.sqEntries.set(JAVA_LONG, base + 32,
            connectionInfoOf(
                clientFd, State.READ, bufferNumber
            ) /* user_data */
        );

        ring.sqArray.setAtIndex(JAVA_INT, index, index);
        ring.sqTail.set(JAVA_INT, 0, tail + 1);
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

        var tail = ring.sqTail.get(JAVA_INT, 0);
        var index = tail & ring.sqRingMask.get(JAVA_INT, 0);

//        var sqe = MemorySegment.ofAddress(
//            ring.sqEntries.address() + (long) index * 64
//        ).reinterpret(64);

        var base = (long) index * 64;

        ring.sqEntries.set(JAVA_BYTE, base + 0, (byte) 26 /* op = IORING_OP_SEND */);
        ring.sqEntries.set(JAVA_BYTE, base + 1, (byte) 0 /* flags */);
        ring.sqEntries.set(JAVA_INT, base + 4, clientFd /* fd */);
        ring.sqEntries.set(JAVA_LONG, base + 8, 0 /* off */);
        ring.sqEntries.set(
            JAVA_LONG, base + 16, buffers.address() + (long) BUFFER_SIZE * bufferNumber /* addr */
        );
        ring.sqEntries.set(JAVA_INT, base + 24, (int) HELLO_RESPONSE.byteSize() - 1 /* len */);
        ring.sqEntries.set(JAVA_LONG, base + 32,
            connectionInfoOf(
                clientFd, State.WRITE, bufferNumber
            ) /* user_data */
        );

        ring.sqArray.setAtIndex(JAVA_INT, index, index);
        ring.sqTail.set(JAVA_INT, 0, tail + 1);
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

    // TODO: park / unpark safely
    // 1. я хочу запарковаться - ставлю global переменную
    // 2. while(!condition) park()
    // 3. Убираю global переменную
    // --- Второй поток:
    // if (needToPark) unpark()
    //


    static void workerF(
        int socketFd, MemorySegment clientAddress, MemorySegment clientAddressLength,
        AtomicLongArray sync, URing ring,
        short[] alloc, MemorySegment buffers, Thread[] threads
    ) throws Throwable {

        var submissionCount = 1;
        var allocCurrent = 0;
        var toExecuteQueue = new long[2048];
        var ei = 0;

        while (true) {
            io_uring_enter(ring.fd, submissionCount, 0, 1 /* IORING_ENTER_GETEVENTS */);
            submissionCount = 0;

            var head = ring.cqHead.get(JAVA_INT, 0);
            do {
                if (head == ring.cqTail.get(JAVA_INT, 0))
                    break;

                var cqe = MemorySegment.ofAddress(
                    ring.cqEntries.address() + (long) (head & ring.cqRingMask.get(JAVA_INT, 0)) * 16
                ).reinterpret(16);

                // accept -> allocate buffer -> read -> write
                // userdata: 8 byte
                //     OP: accept | read | write
                //

                var connectionInfo = cqe.get(JAVA_LONG, 0);
                switch (stateIn(connectionInfo)) {
                    case ACCEPT -> {
                        var clientFd = cqe.get(JAVA_INT, 8);
                        if (clientFd > 0) { // accept without error. FIXME: log ???
                            // System.out.println("fd a: " + clientFd);
                            // System.out.println("ac = " + allocCurrent);
                            submitRead(ring, buffers, clientFd, alloc[allocCurrent++]);
                            submissionCount++;
                        }

                        submitAccept(ring, socketFd, clientAddress, clientAddressLength);
                        submissionCount++;
                    }
                    case READ -> {
                        var ret = cqe.get(JAVA_INT, 8);
                        if (ret <= 0) { /* 0 means END OF FILE ??? */
                            // System.out.println("fd c: " + fdIn(connectionInfo));
                            // System.out.println("dec");
                            alloc[--allocCurrent] = bufferNumberIn(connectionInfo);
                            // TODO: close fd
                        } else {

                            // System.out.println("buffer n after accept: " + bufferNumberIn(connectionInfo));
                            toExecuteQueue[++ei] = connectionInfo;
                        }
                    }
                    case WRITE -> {
                        var ret = cqe.get(JAVA_INT, 8);
                        if (ret <= 0) {
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

            // Thread.sleep(1000);

            for (var i = 0; i < N_IO; i++) {
                var cmd = sync.getAcquire(i * PAD);

                // System.out.println("cmd: " + (int) (cmd & 0x00000000_0000_FFFFL));

                switch ((int) (cmd & 0x00000000_0000_FFFFL)) {
                    case 0: /* ready to execute */
                        if (ei == 0) {
                            // System.out.println("no work");
                            break;
                        }
                        var index = ei--;
                        var bufferNumber = (long) bufferNumberIn(toExecuteQueue[index]);
                        // System.out.println("buffer: " + bufferNumber);
                        var fd = (long) fdIn(toExecuteQueue[index]);

                        // System.out.println("fd e: " + fd);

                        sync.setRelease(
                            i * PAD, fd << 32 | bufferNumber << 16 | 0x00000000_0000_0001L
                        );
                        LockSupport.unpark(threads[i]);
                        break;
                    case 1: /* executing */
                        break;
                    case 2: /* executed */
                        var fd2 = (int) ((cmd & 0xFFFFFFFF_0000_0000L) >> 32);
                        var bufferNumber2 = (short) ((cmd & 0x00000000_FFFF_0000L) >> 16);
                        // System.out.println("fd w: " + fd2);
                        submitWrite(ring, buffers, fd2, bufferNumber2);
                        sync.setRelease(i * PAD, 0);
                        submissionCount++;
                        break;
                    default:
                        throw new RuntimeException("unreachable");
                }
            }
        }

//        while (true) {
//            // 1. не нужно засыпать если у меня не пустая CQ
//            // 2. если у меня не нулевой job count то засыпаем в busy wait
//            //
//
//            // simplify: just iterate over all CQ
//
//            var head = ring.cqHead.get(JAVA_INT, 0);
//            var spinCount = 0;
//            while (
//                (head = ring.cqHead.get(JAVA_INT, 0)) ==
//                ring.cqTail.get(JAVA_INT, 0)
//            ) {
//                if (state.jobsInWorkCount != 0) {
//                    io_uring_enter(
//                        ring.fd, state.submissionCount, 0, 1 /* IORING_ENTER_GETEVENTS */
//                    );
//                    state.submissionCount = 0;
//                    spinCount++;
//
//                    if (spinCount == 2) {
//                        spinCount = 0;
//                        LockSupport.parkNanos(100);
//                        // Thread.yield();
////                        System.out.println("park");
////                        spinCount = 0;
////                        state.parked = Thread.currentThread();
////                        LockSupport.park();
//                    }
//                } else {
//                    io_uring_enter(
//                        ring.fd, state.submissionCount, 1, 1 /* IORING_ENTER_GETEVENTS */
//                    );
//                    state.submissionCount = 0;
//                }
//            }
//
//            var cqe = MemorySegment.ofAddress(
//                ring.cqEntries.address() + (long) (head & ring.cqRingMask.get(JAVA_INT, 0)) * 16
//            ).reinterpret(16);
//
//            ring.cqHead.set(JAVA_INT, 0, head + 1);
//
//            var connectionInfo = cqe.get(JAVA_LONG, 0);
//            switch (stateIn(connectionInfo)) {
//                case ACCEPT -> {
//                    var clientFd = cqe.get(JAVA_INT, 8);
//                    if (clientFd > 0) { // accept without error. FIXME: log ???
//                        submitRead(ring, buffers, clientFd, alloc[state.allocCurrent++]);
//                        state.submissionCount++;
//                    }
//
//                    submitAccept(ring, socketFd, clientAddress, clientAddressLength);
//                    state.submissionCount++;
//                }
//                case READ -> {
//                    var ret = cqe.get(JAVA_INT, 8);
//                    if (ret <= 0) { /* 0 means END OF FILE ??? */
//                        alloc[--state.allocCurrent] = bufferNumberIn(connectionInfo);
//                        // TODO: close fd
//                    } else {
//
//                        // unassigned queue - park
//                        // 1. читаем буфер - если он чей-то, то делаем unpark
//                        // 2. даем unpark всем, кто в unassigned queue
//                        // 3. записываем себя в unassigned queue, делаем park себя
//
//
//                        // push into ready to execute buffers queue
//                        //
//
//
//                        var xx = 1;
//
//
//                        submitWrite(
//                            ring, buffers, fdIn(connectionInfo),
//                            bufferNumberIn(connectionInfo)
//                        );
//                        state.submissionCount++;
//                    }
//                }
//                case WRITE -> {
//                    var ret = cqe.get(JAVA_INT, 8);
//                    if (ret < 0) {
//                        alloc[--state.allocCurrent] = bufferNumberIn(connectionInfo);
//                        // TODO: close fd
//                    } else {
//                        submitRead(
//                            ring, buffers, fdIn(connectionInfo),
//                            bufferNumberIn(connectionInfo)
//                        );
//                        state.submissionCount++;
//                    }
//                }
//            }
//        }
        // for each IO_THREAD
        //   switch(cmd) {
        //       case 0: /* ready to execute */ supply buffer
        //       case 1: /* executing */ skip
        //       case 2: /* executed */ submitWrite(executedBuffer); supply buffer
        //   }
        //
        // io_uring_enter()
        // первая версия busy wait!
        //
    }

    static int PAD = 16;
    static int N_THREADS = 1;
    static int N_IO = 80;

    static class S {
        int submissionCount;
        int allocCurrent;
        int jobsInWorkCount;
    }

    static void main() throws Throwable {

//        System.setProperty("jdk.pollerMode", "SYSTEM_THREADS");
//        System.setProperty("jdk.readPollers", "2");
//        System.setProperty("jdk.writePollers", "1");

        System.setProperty("jdk.virtualThreadScheduler.parallelism", "8");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "8");

        var connections = new Connection[N_THREADS * N_IO];
        for (var i = 0; i < connections.length; i++)
            connections[i] = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/petclinic?binaryTransfer=false&sslmode=disable",
                "sa", "1234"
            );


        for (var i = 0; i < N_THREADS; i++) {
            var threads = new Thread[N_IO];
            var ii = i;

            var sync = new AtomicLongArray(N_IO * PAD);
            for (var k = 0; k < N_IO; k++)
                sync.set(k * PAD, 0L);

            var buffers = Arena.global().allocate((long) MAX_CONNECTIONS * BUFFER_SIZE, 4096);


            var ioThread = new Thread(() -> {
                try {

                    var alloc = new short[MAX_CONNECTIONS];
                    for (int k = 0; k < alloc.length; k++)
                        alloc[k] = (short) k;



//                    var buffers = mmap(
//                        NULL, (long) MAX_CONNECTIONS * BUFFER_SIZE,
//                        0x1 /* PROT_READ */ | 0x2 /* PROT_WRITE */,
//                        0x1 /* MAP_SHARED */ | 0x20 /* MAP_ANONYMOUS */,
//                        -1, 0
//                    ).reinterpret((long) MAX_CONNECTIONS * BUFFER_SIZE);

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




                    workerF(
                        socketFd, clientAddress, clientAddressLength,
                        sync, ring, alloc, buffers, threads
                    );
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
            ioThread.start();

            // commands
            // 0 - ready to execute
            // 1 - buffer to execute
            // 2 - buffer executed
            //
            var a = 0x00000000_0000_0000L;
            var b = 0x00000000_0000_0001L;
            var c = 0x00000000_0000_0002L;


            for (var j = 0; j < N_IO; j++) {
                var jj = j;

                threads[jj] = Thread.ofVirtual().start(() -> {
                    while (true) {

                        var cmd = 0L;


                        while (((cmd = sync.getAcquire(jj * PAD)) & 0x00000000_0000_FFFFL) != 1)
                            LockSupport.park();

                        var bufferNumber = (cmd & 0x00000000_FFFF_0000L) >> 16;
                        var fd = (cmd & 0xFFFFFFFF_0000_0000L) >> 32;

//                        if (
//                            MemorySegment.mismatch(
//                                buffers, (long) BUFFER_SIZE * bufferNumber, (long) BUFFER_SIZE * bufferNumber + HELLO_REQUEST_LINE.byteSize(),
//                                HELLO_REQUEST_LINE, 0, HELLO_REQUEST_LINE.byteSize()
//                            ) != -1
//                        )
//                            throw new RuntimeException("NOT FOUND");


                        try {
                            var cn = connections[ii * N_IO + jj];
                            try (var stmt = cn.createStatement()) {
                                stmt.execute("select 1");
                            }

//                            try (var stmt = cn.prepareStatement("select 1")) {
//                                stmt.execute();
//                            } catch (SQLException e) {
//                                throw new RuntimeException(e);
//                            }

                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }

                        MemorySegment.copy(
                            HELLO_RESPONSE, 0, buffers,
                            (long) BUFFER_SIZE * bufferNumber, HELLO_RESPONSE.byteSize() - 1
                        );
                        // System.out.println("executed: " + bufferNumber);

                        sync.setRelease(
                            jj * PAD, (fd << 32) | (bufferNumber << 16) | 0x00000000_0000_0002L
                        );
                    }
                });
            }
            // threads[i].start();
        }


        Thread.sleep(1000000000);
    }
}
