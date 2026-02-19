import java.lang.foreign.*;

import static java.lang.foreign.MemorySegment.NULL;
import static java.lang.foreign.ValueLayout.*;
import static net.truej.service.low.LibC.*;

public class TestIoUringHello {
    //@formatter:off
    //@formatter:on

    static long MAX_CONNECTIONS = 1024;
    static long BUFFER_SIZE = 128;


    static void main() throws Throwable {
        var buffers = mmap(
            NULL, MAX_CONNECTIONS * BUFFER_SIZE,
            0x1 /* PROT_READ */ | 0x2 /* PROT_WRITE */,
            0x1 /* MAP_SHARED */ | 0x20 /* MAP_ANONYMOUS */,
            -1, 0
        ).reinterpret(MAX_CONNECTIONS * BUFFER_SIZE);

        var socketFd = socket(/* AF_INET */ 2, /* SOCK_STREAM */ 1, 0);

        setsockopt(socketFd, /* SOL_SOCKET */ 1, /* SO_REUSEADDR */ 2, 1);
        setsockopt(socketFd, /* SOL_SOCKET */ 1, /* SO_REUSEPORT */ 15, 1);
        setsockopt(socketFd, /* SOL_TCP    */ 6, /* TCP_NODELAY  */ 1, 1);

        bind(socketFd, new byte[]{127, 0, 0, 1}, (short) 7777);
        listen(socketFd, 512);

        var ring = io_uring_setup(2048);

        var buff = Arena.global().allocateFrom("Hello\n");
        var devNullFd = open("/dev/null");

        while (true) {
            var tail = ring.sqTail.get(JAVA_INT, 0);
            var index = tail & ring.sqRingMask.get(JAVA_INT, 0);
            var sqe = MemorySegment.ofAddress(
                ring.sqEntries.address() + (long) index * 64
            ).reinterpret(64);

            sqe.set(JAVA_BYTE, 0, (byte) 23 /* op = IORING_OP_WRITE */);
            sqe.set(JAVA_BYTE, 1, (byte) 0 /* flags */);
            sqe.set(JAVA_INT, 4, devNullFd /* fd */);
            sqe.set(JAVA_LONG, 16, buff.address() /* addr */);
            sqe.set(JAVA_INT, 24, 6 /* len */);

            ring.sqArray.setAtIndex(JAVA_INT, index, index);
            ring.sqTail.set(JAVA_INT, 0, tail + 1);


            var head = ring.cqHead.get(JAVA_INT, 0);
            do {
                if (head == ring.cqTail.get(JAVA_INT, 0))
                    break;
                head++;
            } while (true);

            ring.cqHead.set(JAVA_INT, 0, head);
            io_uring_enter(ring.fd, 1, 1, 0);
        }


//        System.out.println(socketFd);
//        System.out.println(ring);
//
//        Thread.sleep(100_000_000);


        // 1.
        //  - perror
        //  - socket
        //  - setsockopt
        //  - htons
        //  - bind
        //  - listen
        //  - syscall[0,6]
        // 2.
        //  - IO_URING kernel API
        //    - ring init & buffers
        //    - submissons & completions
        //    - operations builder

    }
}
