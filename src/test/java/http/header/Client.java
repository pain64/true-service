package http.header;

import internalapi.CheetahApi;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Client implements CheetahApi {
    private final byte[] request;
    private int requestPointer = 0;
    private final int requestChunkSize;
    private final MemorySegment requestMemorySegment;

    private final ArrayList<MemorySegment> response = new ArrayList<>();

    public Client(String request, int requestChunkSize) {
        this.request = request.getBytes(StandardCharsets.UTF_8);
        this.requestChunkSize = requestChunkSize;
        this.requestMemorySegment = MemorySegment.ofArray(new byte[requestChunkSize]);
    }

    @Override
    public @Nullable MemorySegment read() {
        if (requestPointer == requestChunkSize) return null;

        for (var i = 0; (i < requestChunkSize) && (i < requestPointer++); i++) {
            requestMemorySegment.setAtIndex(ValueLayout.JAVA_BYTE, i, request[i]);
        }

        return requestMemorySegment;
    }

    @Override
    public void write(MemorySegment buffer) {
        response.add(buffer);
    }

    public String getResponse() {
        var size = response.stream().map(MemorySegment::byteSize).reduce(0L, Long::sum).intValue();
        var result = new byte[size];
        var resultPosition = 0;

        for (var ms: response) {
            for (var i = 0; i < ms.byteSize(); i++) {
                result[resultPosition++] = ms.getAtIndex(ValueLayout.JAVA_BYTE, i);
            }
        }

        return new String(result, StandardCharsets.UTF_8);
    }
}
