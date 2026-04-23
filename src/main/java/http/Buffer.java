package http;

import java.nio.charset.StandardCharsets;

public class Buffer {
    public final byte[] bytes;
    private int remains = 0;

    public Buffer(int bufferByteSize) {
        this.bytes = new byte[bufferByteSize];
    }

    public int remains() {
        return remains;
    }

    public void push(byte b) {
        bytes[remains++] = b;
    }

    public void reset() { remains = 0; }

    public String toStringAndReset() {
        var s = new String(bytes, 0, remains, StandardCharsets.UTF_8);
        reset();
        return s;
    }
}
