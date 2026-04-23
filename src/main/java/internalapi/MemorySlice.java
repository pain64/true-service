package internalapi;

import java.lang.foreign.MemorySegment;

public class MemorySlice {
    public final MemorySegment segment;

    private long offset;
    private long length;

    public MemorySlice(MemorySegment segment, long offset, long length) {
        this.segment = segment;
        this.offset = offset;
        this.length = length;
    }

    public void setOffset(long offset) {
        if (offset < this.offset) throw new RuntimeException("Hehehe");
        this.length = this.length - (offset - this.offset);
        this.offset = offset;
    }

}
