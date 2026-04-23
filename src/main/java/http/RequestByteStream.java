package http;

import internalapi.CheetahApi;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class RequestByteStream {
    private static final int MAX_REQUEST_BUFFER_SIZE = 2048;
    private final CheetahApi cheetahApi;

    private int requestPosition = 0;

    private MemorySegment currentMS;
    private int msPosition = 0;

    private final byte[] msBuffer = new byte[MAX_REQUEST_BUFFER_SIZE];
    private int msBufferPosition = 0;
    private int msBufferEnd = 0;

    public RequestByteStream(CheetahApi cheetahApi) {
        this.cheetahApi = cheetahApi;
        this.currentMS = cheetahApi.read();
//        var a = new byte[10];
//        for (var i = 0; i < 10; i++) {
//            a[i] = currentMS.getAtIndex(ValueLayout.JAVA_BYTE, i);
//        }
//        var s = new String(a);
    }

    private int msAvail() {
        return (int) currentMS.byteSize() - msPosition;
    }

    private int bufferAvail() {
        return (msBufferEnd - msBufferPosition) + (msBufferPosition <= msBufferEnd ? 0: msBuffer.length);
    }

    private byte getByteFromBuffer() {
        var byteToReturn = msBuffer[msBufferPosition++];
        if (msBufferPosition == msBuffer.length) msBufferPosition = 0;

        return byteToReturn;
    }

    public byte current() {
        if (bufferAvail() == 0)
            return currentMS.getAtIndex(ValueLayout.JAVA_BYTE, msPosition);
        else return msBuffer[msBufferPosition];
    }

    public byte advance() {
        if (bufferAvail() == 0) {
            var toReturn = currentMS.getAtIndex(ValueLayout.JAVA_BYTE, msPosition++);
            requestPosition++;

            if (msPosition == currentMS.byteSize()) {
                currentMS = cheetahApi.read();
                msPosition = 0;
            }
            return toReturn;
        } else return getByteFromBuffer();
    }

    public byte lookahead(int idx) {
        // на случай если не хватает данных
        if (idx > bufferAvail() + msAvail()-1) {
            var bufferToAddCount = idx + 1 - bufferAvail() - msAvail();
            if (bufferToAddCount > MAX_REQUEST_BUFFER_SIZE - bufferAvail()) throw new RuntimeException("Too big idx to lookahead");

            while (bufferToAddCount > 0) {
                if (msBufferEnd == msBuffer.length - 1) msBufferEnd = 0;

                msBuffer[msBufferEnd++] = currentMS.getAtIndex(ValueLayout.JAVA_BYTE, msPosition++);
                bufferToAddCount--;

                if (msAvail() == 0) {
                    currentMS = cheetahApi.read();
                    msPosition = 0;
                }
            }
        }

        var msAvail = msAvail();
        var bfrAvail = bufferAvail();

        if (bfrAvail == 0 && idx < msAvail)
            return currentMS.getAtIndex(ValueLayout.JAVA_BYTE, msPosition + idx);
        else if (idx < bfrAvail)
            return msBuffer[msBufferEnd > msBufferPosition ? msBufferPosition + idx: (msBufferPosition + idx) % msBuffer.length];
        else return currentMS.getAtIndex(ValueLayout.JAVA_BYTE, msPosition + idx - bfrAvail);
    }

    public void movePosition(int N) {
        var bfrAvail = bufferAvail();

        if (bfrAvail == 0) {
            msPosition += N;
        } else if (bfrAvail >= N) {
            msBufferPosition = (msBufferPosition < msBufferEnd ? msBufferPosition + N : (msBufferPosition + N) % msBuffer.length);
        } else {
            msBufferPosition = msBufferEnd;
            msPosition += (N - bfrAvail);
        }
    }

    public int requestPosition() {return requestPosition;}

    public MemorySegment getMsTail() {
        if (msPosition == currentMS.byteSize()) return null;
        return currentMS.asSlice(msPosition, currentMS.byteSize() - msPosition);
    }
}
