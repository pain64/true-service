package net.truej.service.low;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.foreign.MemorySegment;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;

public class MyOutputStream extends OutputStream {
    private final IoUringEventLoop eventLoop;
    private final short socketId;
    private final MemorySegment writeBuffer;
    private int offset = 0;

    public MyOutputStream(IoUringEventLoop eventLoop, short socketId) {
        this.eventLoop = eventLoop;
        this.socketId = socketId;
        this.writeBuffer = eventLoop.writeBuffer(socketId);
    }

    @Override public void write(int i) throws IOException {
        throw new RuntimeException("not impl");
    }

    @Override public void write(@NotNull byte[] b, int off, int len) throws IOException {
        // FIXME: если размер b больше чем writeBuffer, то нужно отправить частями
        MemorySegment.copy(
            b, off, writeBuffer, JAVA_BYTE, offset, len
        );
        offset += len;
    }

    @Override public void flush() {
        if (offset != 0)
            eventLoop.awaitSocketWrite(socketId, offset);
        offset = 0;
    }
}
