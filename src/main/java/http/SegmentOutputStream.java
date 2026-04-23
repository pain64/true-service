package http;

import internalapi.CheetahApi;
import internalapi.MemorySegmentOutputStream;

import java.lang.foreign.MemorySegment;

public class SegmentOutputStream implements MemorySegmentOutputStream {
    private final CheetahApi cheetahApi;

    public SegmentOutputStream(CheetahApi cheetahApi, MemorySegment tail) {
        this.cheetahApi = cheetahApi;
    }

    @Override
    public void push(MemorySegment buffer) {
        cheetahApi.write(buffer);
    }
}
