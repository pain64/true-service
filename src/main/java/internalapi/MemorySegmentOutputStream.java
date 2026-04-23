package internalapi;

import java.lang.foreign.MemorySegment;

public interface MemorySegmentOutputStream {

    void push(MemorySegment buffer);
}
