package internalapi;

import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;

public interface MemorySegmentInputStream {
    // null - end of stream
    @Nullable MemorySegment read();
}
