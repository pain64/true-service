package http;

import internalapi.CheetahApi;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;

public class ResponseByteStream {
    private final CheetahApi cheetahApi;
    private MemorySegment currentMS; //TODO: откуда-то надо взять
    private int msPosition = 0;

    public ResponseByteStream(CheetahApi cheetahApi) {
        this.cheetahApi = cheetahApi;
    }

    public void checkMSAvail() {
        if (msPosition == currentMS.byteSize()) {
            cheetahApi.write(currentMS);
            msPosition = 0;
        }
    }

    public void push(char ch) {
        checkMSAvail();
        currentMS.setAtIndex(ValueLayout.JAVA_BYTE, msPosition++, (byte) ch);
    }

    public void push(byte[] v) {
        for (var b : v) {
            checkMSAvail();
            currentMS.setAtIndex(ValueLayout.JAVA_BYTE, msPosition++, b);
        }
    }

    public void push(String v) {
        for (var b : v.getBytes(StandardCharsets.UTF_8)) {
            checkMSAvail();
            currentMS.setAtIndex(ValueLayout.JAVA_BYTE, msPosition++, (byte) b);
        }
    }

    public void pushTail() {
        if (msPosition != 0) cheetahApi.write(currentMS); // TODO, as slice
    }

}
