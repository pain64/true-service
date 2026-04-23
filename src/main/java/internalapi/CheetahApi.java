package internalapi;

import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;

public interface CheetahApi {
    // после нового read ссылка на старый MemorySegment больше не твоя

    // null - end of stream
    @Nullable MemorySegment read();
    void write(MemorySegment buffer);
}
