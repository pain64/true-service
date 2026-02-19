package net.truej.service.low;

import lombok.Data;

import java.io.Closeable;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.foreign.MemorySegment.*;
import static java.lang.foreign.ValueLayout.*;

public class LibC {
    private static final Arena ARENA = Arena.global();
    private static final Linker LINKER = Linker.nativeLinker();
    private static final SymbolLookup LIB_C = SymbolLookup.libraryLookup("libc.so.6", ARENA);


    private static final MethodHandle H_SYSCALL2 = LINKER.downcallHandle(
        LIB_C.find("syscall").get(),
        FunctionDescriptor.of(
            /* ret    */ JAVA_LONG,
            /* number */ JAVA_LONG,
            /* param1 */ JAVA_LONG,
            /* param2 */ JAVA_LONG
        )
    );

    private static final MethodHandle H_SYSCALL6 = LINKER.downcallHandle(
        LIB_C.find("syscall").get(),
        FunctionDescriptor.of(
            /* ret    */ JAVA_LONG,
            /* number */ JAVA_LONG,
            /* param1 */ JAVA_LONG,
            /* param2 */ JAVA_LONG,
            /* param3 */ JAVA_LONG,
            /* param4 */ JAVA_LONG,
            /* param5 */ JAVA_LONG,
            /* param6 */ JAVA_LONG
        )
    );

    private static final MethodHandle H_ERRNO_FUNCTION = LINKER.downcallHandle(
        LIB_C.find("__errno_location").get(),
        FunctionDescriptor.of(
            /* ret      */ ADDRESS.withTargetLayout(
                MemoryLayout.sequenceLayout(1, JAVA_INT)
            )
        )
    );

    private static final MethodHandle H_STRERROR = LINKER.downcallHandle(
        LIB_C.find("strerror").get(),
        FunctionDescriptor.of(
            /* ret      */ ADDRESS.withTargetLayout(
                MemoryLayout.sequenceLayout(Long.MAX_VALUE, JAVA_BYTE)
            ),
            /* errnum   */ JAVA_INT
        )
    );

    private static final MethodHandle H_SOCKET = LINKER.downcallHandle(
        LIB_C.find("socket").get(),
        FunctionDescriptor.of(
            /* ret      */ JAVA_INT,
            /* domain   */ JAVA_INT,
            /* type     */ JAVA_INT,
            /* protocol */ JAVA_INT
        )
    );

    private static final MethodHandle H_SETSOCKOPT = LINKER.downcallHandle(
        LIB_C.find("setsockopt").get(),
        FunctionDescriptor.of(
            /* ret     */ JAVA_INT,
            /* sockfd  */ JAVA_INT,
            /* level   */ JAVA_INT,
            /* optname */ JAVA_INT,
            /* optval  */ ADDRESS,
            /* optlen  */ JAVA_LONG
        )
    );

    private static final MethodHandle H_HTONS = LINKER.downcallHandle(
        LIB_C.find("htons").get(),
        FunctionDescriptor.of(
            /* ret       */ JAVA_SHORT,
            /* hostshort */ JAVA_SHORT
        )
    );

    private static final MethodHandle H_BIND = LINKER.downcallHandle(
        LIB_C.find("bind").get(),
        FunctionDescriptor.of(
            /* ret     */ JAVA_INT,
            /* sockfd  */ JAVA_INT,
            /* addr    */ ADDRESS,
            /* addrlen */ JAVA_LONG
        )
    );

    private static final MethodHandle H_CONNECT = LINKER.downcallHandle(
        LIB_C.find("connect").get(),
        FunctionDescriptor.of(
            /* ret     */ JAVA_INT,
            /* sockfd  */ JAVA_INT,
            /* addr    */ ADDRESS,
            /* addrlen */ JAVA_LONG
        )
    );

    private static final MethodHandle H_LISTEN = LINKER.downcallHandle(
        LIB_C.find("listen").get(),
        FunctionDescriptor.of(
            /* ret     */ JAVA_INT,
            /* sockfd  */ JAVA_INT,
            /* backlog */ JAVA_INT
        )
    );

    private static final MethodHandle H_MMAP = LINKER.downcallHandle(
        LIB_C.find("mmap").get(),
        FunctionDescriptor.of(
            /* ret    */ ADDRESS.withTargetLayout(
                MemoryLayout.sequenceLayout(Long.MAX_VALUE, JAVA_BYTE)
            ),
            /* addr   */ ADDRESS,
            /* length */ JAVA_LONG,
            /* prot   */ JAVA_INT,
            /* flags  */ JAVA_INT,
            /* fd     */ JAVA_INT,
            /* offset */ JAVA_LONG
        )
    );

    // int open(const char *pathname, int flags);

    private static final MethodHandle H_OPEN = LINKER.downcallHandle(
        LIB_C.find("open").get(),
        FunctionDescriptor.of(
            /* ret      */ JAVA_INT,
            /* pathname */ ADDRESS,
            /* flags    */ JAVA_INT
        )
    );

    private static final MethodHandle H_CLOSE = LINKER.downcallHandle(
        LIB_C.find("close").get(),
        FunctionDescriptor.of(
            /* ret  */ JAVA_INT,
            /* fd */ JAVA_INT
        )
    );

    public static int open(String path) throws Throwable {
        try (var arena = Arena.ofConfined()){
            var ret = (int) H_OPEN.invokeExact(arena.allocateFrom(path), 0);
            if (ret < 0) throw new CFunctionInvocationError(errno());
            return ret;
        }
    }

    public static void close(int fd) throws Throwable {
        var ret = (int) H_CLOSE.invokeExact(fd);
        if (ret == -1) throw new CFunctionInvocationError(errno());
    }

    public static int errno() throws Throwable {
        return ((MemorySegment) H_ERRNO_FUNCTION.invokeExact()).getAtIndex(JAVA_INT, 0);
    }

    public static String strerror(int errnum) throws Throwable {
        return ((MemorySegment) H_STRERROR.invokeExact(errnum)).getString(0);
    }

    public static int socket(int domain, int type, int protocol) throws Throwable {
        var ret = (int) H_SOCKET.invokeExact(domain, type, protocol);
        if (ret == -1) throw new CFunctionInvocationError(errno());
        return ret;
    }

    public static void setsockopt(
        int sockfd, int level, int optname, MemorySegment optval, long optlen
    ) throws Throwable {
        var ret = (int) H_SETSOCKOPT.invokeExact(sockfd, level, optname, optval, optlen);
        if (ret == -1) throw new CFunctionInvocationError(errno());
    }

    public static void setsockopt(
        int sockfd, int level, int optname, int ival
    ) throws Throwable {
        try (var arena = Arena.ofConfined()) {
            setsockopt(sockfd, level, optname, arena.allocateFrom(JAVA_INT, ival), 4);
        }
    }

    public static void connect(int sockfd, byte[] address, short port) throws Throwable {
        try (var arena = Arena.ofConfined()) {
            var v4Address = arena.allocate(16, 8);

            v4Address.set(JAVA_SHORT, 0, /* AF_INET */ (short) 2 /* .sin_family */);
            v4Address.set(JAVA_SHORT, 2, htons(port) /* .sin_port */);
            v4Address.set(JAVA_BYTE, 4, address[0] /* .sin_addr.s_addr */);
            v4Address.set(JAVA_BYTE, 5, address[1] /* .sin_addr.s_addr */);
            v4Address.set(JAVA_BYTE, 6, address[2] /* .sin_addr.s_addr */);
            v4Address.set(JAVA_BYTE, 7, address[3] /* .sin_addr.s_addr */);

            var ret = (int) H_CONNECT.invokeExact(sockfd, v4Address, 16L);
            if (ret == -1) throw new CFunctionInvocationError(errno());
        }
    }

    public static short htons(short hostshort) throws Throwable {
        return (short) H_HTONS.invokeExact(hostshort);
    }

    public static void bind(int sockfd, byte[] address, short port) throws Throwable {
        try (var arena = Arena.ofConfined()) {
            var v4Address = arena.allocate(16, 8);

            v4Address.set(JAVA_SHORT, 0, /* AF_INET */ (short) 2 /* .sin_family */);
            v4Address.set(JAVA_SHORT, 2, htons(port) /* .sin_port */);
            v4Address.set(JAVA_BYTE, 4, address[0] /* .sin_addr.s_addr */);
            v4Address.set(JAVA_BYTE, 5, address[1] /* .sin_addr.s_addr */);
            v4Address.set(JAVA_BYTE, 6, address[2] /* .sin_addr.s_addr */);
            v4Address.set(JAVA_BYTE, 7, address[3] /* .sin_addr.s_addr */);

            var ret = (int) H_BIND.invokeExact(sockfd, v4Address, 16L);
            if (ret == -1) throw new CFunctionInvocationError(errno());
        }
    }

    public static void listen(int sockfd, int backlog) throws Throwable {
        var ret = (int) H_LISTEN.invokeExact(sockfd, backlog);
        if (ret == -1) throw new CFunctionInvocationError(errno());
    }

    public static MemorySegment mmap(
        MemorySegment addr, long length, int prot, int flags, int fd, long offset
    ) throws Throwable {
        var ptr = (MemorySegment) H_MMAP.invokeExact(addr, length, prot, flags, fd, offset);
        if (ptr.address() == 0) throw new CFunctionInvocationError(errno());
        return ptr;
    }

    @Data public static class URing implements Closeable {
        public final int fd;

        public final MemorySegment sqHead;
        public final MemorySegment sqTail;
        public final MemorySegment sqRingMask;
        public final MemorySegment sqArray;
        public final MemorySegment sqEntries;

        public final MemorySegment cqHead;
        public final MemorySegment cqTail;
        public final MemorySegment cqRingMask;
        public final MemorySegment cqEntries;

        private void submit(Consumer<MemorySegment> sqeConsumer) {
            var tail = sqTail.get(JAVA_INT, 0);
            var index = tail & sqRingMask.get(JAVA_INT, 0);

            sqeConsumer.accept(
                MemorySegment.ofAddress(
                    sqEntries.address() + (long) index * 64
                ).reinterpret(64)
            );

            sqArray.setAtIndex(JAVA_INT, index, index);
            sqTail.set(JAVA_INT, 0, tail + 1);
        }

        public void submitAccept(
            int socketFd, MemorySegment clientAddress,
            MemorySegment clientAddressLength, long userData
        ) {
            submit(sqe -> {
                sqe.set(JAVA_BYTE, 0, (byte) 13 /* op = IORING_OP_ACCEPT */);
                sqe.set(JAVA_BYTE, 1, (byte) 0 /* flags */);
                sqe.set(JAVA_INT, 4, socketFd /* fd */);
                sqe.set(JAVA_LONG, 8, clientAddressLength.address() /* off */);
                sqe.set(JAVA_LONG, 16, clientAddress.address() /* addr */);
                sqe.set(JAVA_INT, 24, 0 /* len */);
                sqe.set(JAVA_LONG, 32, userData /* user_data */);
            });
        }

        public void submitRead(int fd, MemorySegment buffer, int len, long userData) {
            submit(sqe -> {
                if (fd == 0) throw new RuntimeException("oops");
                sqe.set(JAVA_BYTE, 0, (byte) 27 /* op = IORING_OP_RECV */);
                sqe.set(JAVA_BYTE, 1, (byte) 0 /* flags */);
                sqe.set(JAVA_INT, 4, fd /* fd */);
                sqe.set(JAVA_LONG, 8, 0 /* off */);
                sqe.set(
                    JAVA_LONG, 16, buffer.address() /* addr */
                );
                sqe.set(JAVA_INT, 24, len /* len */);
                sqe.set(JAVA_LONG, 32, userData /* user_data */);
            });
        }

        public void submitWrite(int fd, MemorySegment buffer, int len, long userData) {
            submit(sqe -> {
                if (fd == 0) throw new RuntimeException("oops");
                sqe.set(JAVA_BYTE, 0, (byte) 26 /* op = IORING_OP_SEND */);
                sqe.set(JAVA_BYTE, 1, (byte) 0 /* flags */);
                sqe.set(JAVA_INT, 4, fd /* fd */);
                sqe.set(JAVA_LONG, 8, 0 /* off */);
                sqe.set(
                    JAVA_LONG, 16, buffer.address() /* addr */
                );
                sqe.set(JAVA_INT, 24, len /* len */);
                sqe.set(JAVA_LONG, 32, userData /* user_data */);
            });
        }

        public void enter(int nSubmit, int minComplete, int flags) throws Throwable {
            var ret = (int) (long) H_SYSCALL6.invokeExact(
                426L, (long) fd, (long) nSubmit, (long) minComplete, (long) flags, 0L, 0L
            );
            if (ret < 0) throw new CFunctionInvocationError(-ret);
        }

        @Override public void close() {
            // TODO
        }
    }

    public static URing io_uring_setup(int queueDepth) throws Throwable {
        try (var arena = Arena.ofConfined()) {
            var parameters = arena.allocate(120 /* sizeof(io_uring_params) */);
            parameters.fill((byte) 0);

            var ringFd = (int) (long) H_SYSCALL2.invokeExact(
                425L, (long) queueDepth, parameters.address()
            );
            if (ringFd == -1) throw new CFunctionInvocationError(errno());


            var submissionQueueLength = (long) parameters.get(JAVA_INT, 0 /* .sq_entries */);
            var submissionQueueSize =
                parameters.get(JAVA_INT, 40 + 24 /* .sq_off.array */) +
                submissionQueueLength * 4 /* sizeof(unsigned) */;

            var completionQueueLength = (long) parameters.get(JAVA_INT, 4 /* .cq_entries */);
            var completionQueueSize =
                parameters.get(JAVA_INT, 80 + 20 /* .cq_off.cqes */) +
                completionQueueLength * 16 /* sizeof(io_uring_cqe) */;

            var mmapSize = Math.max(submissionQueueSize, completionQueueSize);

            var ptr1 = mmap(
                NULL, mmapSize, 0x1 /* PROT_READ */ | 0x2 /* PROT_WRITE */,
                0x01 /* MAP_SHARED */ | 0x08000 /* MAP_POPULATE */,
                ringFd, 0 /* IORING_OFF_SQ_RING */
            );

            var ptr2 = mmap(
                NULL, submissionQueueLength * 64, 0x1 /* PROT_READ */ | 0x2 /* PROT_WRITE */,
                0x01 /* MAP_SHARED */ | 0x08000 /* MAP_POPULATE */,
                ringFd, 0x10000000 /* IORING_OFF_SQES */
            ).asSlice(
                0, submissionQueueLength * 64
            );


            var intPtr1 = (Function<Integer, MemorySegment>) offset ->
                MemorySegment.ofAddress(
                    ptr1.address() + parameters.get(JAVA_INT, offset)
                ).reinterpret(4);

            return new URing(
                ringFd,
                intPtr1.apply(40 + 0  /* .sq_off.head      */),
                intPtr1.apply(40 + 4  /* .sq_off.tail      */),
                intPtr1.apply(40 + 8  /* .sq_off.ring_mask */),
                MemorySegment.ofAddress(
                    ptr1.address() + parameters.get(JAVA_INT, 40 + 24 /* .sq_off.array */)
                ).reinterpret(submissionQueueLength * 4),
                ptr2,
                intPtr1.apply(80 + 0  /* .cq_off.head      */),
                intPtr1.apply(80 + 4  /* .cq_off.tail      */),
                intPtr1.apply(80 + 8  /* .cq_off.ring_mask */),
                MemorySegment.ofAddress(
                    ptr1.address() + parameters.get(JAVA_INT, 80 + 20 /* .cq_off.cqes */)
                ).reinterpret(completionQueueLength * 16)
            );
        }
    }

    public static void io_uring_enter(
        int ringFd, int nSubmit, int minComplete, int flags
    ) throws Throwable {
        var ret = (int) (long) H_SYSCALL6.invokeExact(
            426L, (long) ringFd, (long) nSubmit, (long) minComplete, (long) flags, 0L, 0L
        );
        if (ret < 0) throw new CFunctionInvocationError(-ret);
    }
}
