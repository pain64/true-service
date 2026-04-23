package http;

import internalapi.CheetahApi;
import internalapi.MemorySegmentInputStream;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;

public class SegmentInputStream implements MemorySegmentInputStream {
    private final CheetahApi cheetahApi;
    private MemorySegment tail;


    public SegmentInputStream(CheetahApi cheetahApi, MemorySegment tail) {
        this.cheetahApi = cheetahApi;
        this.tail = tail;
    }

    @Override
    public @Nullable MemorySegment read() {
        if (tail != null) {
            var toReturn = tail;
            tail = null;
            return toReturn;
        }

        return cheetahApi.read();
    }
}
