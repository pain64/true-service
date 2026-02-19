package net.truej.service.low;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class MyInputStream extends InputStream {
    private final IoUringEventLoop eventLoop;
    private final short socketId; // TODO: rename to socketIndex
    private final MemorySegment readBuffer;
    private int offset = 0;

    public MyInputStream(IoUringEventLoop eventLoop, short socketId) {
        this.eventLoop = eventLoop;
        this.socketId = socketId;
        this.readBuffer = eventLoop.readBuffer(socketId);
    }

    @Override public int read() {
        throw new RuntimeException("not impl");
    }

    @Override public int read(@NotNull byte[] b) throws IOException {
        throw new RuntimeException("not impl");
    }

    @Override public int read(@NotNull byte[] b, int off, int len) {
        var readBytes = eventLoop.awaitSocketRead(socketId);
        offset = 0;
        // var ooo = Math.max(readBytes, len);

        // TODO: check bounds
        MemorySegment.copy(
            readBuffer, ValueLayout.JAVA_BYTE, offset, b, off, readBytes
        );
        offset += readBytes;
        return readBytes;
    }

    @Override public int available() throws IOException {
        throw new RuntimeException("not impl");
    }
}
