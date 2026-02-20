package http;

import http.Dsl.Headers;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TestParseHttp {

    public enum Method {
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        CONNECT,
        TRACE,
        PATCH
    }

    public class Header {
        final String name;
        final String value;

        public Header(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    final String[] applicableHttpVersions = {"HTTP/1.1"};

    final byte ws = (byte) ' ';
    final byte cr = (byte) '\r';
    final byte lf = (byte) '\n';
    final byte col = (byte) ':';

    final int MAX_METHOD_LENGTH = 10;
    final int MAX_URI_LENGTH = 8000;
    final int MAX_HTTP_VERSION_LENGTH = 10;

    final int MAX_HEADER_LENGTH = 1024;


    public Method getMethod(InputStream is) throws Exception {
        final byte[] buffer = new byte[MAX_METHOD_LENGTH];
        var byteBufferIdx = 0;


        for (var i = 0; i < buffer.length; i++) {
            buffer[byteBufferIdx] = (byte) is.read();
            if (buffer[byteBufferIdx] == ws) {
                //todo invalid method
                return Method.valueOf(new String(Arrays.copyOfRange(buffer, 0, byteBufferIdx-1), StandardCharsets.UTF_8));
            }
            byteBufferIdx++;
        }
        throw new Exception("ivalid start line");
    }

    public URI getURI(InputStream is) throws Exception {
        final byte[] buffer = new byte[MAX_URI_LENGTH];
        var byteBufferIdx = 0;

        for (var i = 0; i < buffer.length; i++) {
            buffer[byteBufferIdx] = (byte) is.read();
            if (buffer[byteBufferIdx] == ws) {
                //todo invalid uri
                return new URI(new String(Arrays.copyOfRange(buffer, 0, byteBufferIdx-1), StandardCharsets.UTF_8));
            }
            byteBufferIdx++;
        }
        throw new Exception("ivalid start line");
    }

    public String getHttpVersion(InputStream is) throws Exception {
        final byte[] buffer = new byte[MAX_HTTP_VERSION_LENGTH];
        var byteBufferIdx = 0;

        for (var i = 0; i < buffer.length; i++) {
            buffer[byteBufferIdx] = (byte) is.read();
            if (buffer[byteBufferIdx] == cr) {
                buffer[byteBufferIdx] = (byte) is.read();
                byteBufferIdx++;
                if (buffer[byteBufferIdx] == lf) {
                    return new String(Arrays.copyOfRange(buffer, 0, byteBufferIdx-2), StandardCharsets.UTF_8);
                }
            }
            byteBufferIdx++;
        }
        throw new Exception("ivalid start line");
    }

    public  Header getHeader(InputStream is, byte[] buffer, int byteBufferIdx) throws Exception {
        var headerNameEnd = 0;

        for (var i = 0; i < buffer.length; i++) {
            buffer[byteBufferIdx] = (byte) is.read();
            if (buffer[byteBufferIdx] == col) {
                headerNameEnd = byteBufferIdx-1;
            }
            if (buffer[byteBufferIdx] == cr) {
                buffer[byteBufferIdx] = (byte) is.read();
                byteBufferIdx++;
                if (buffer[byteBufferIdx] == lf) {
                    return new Header(
                        new String(Arrays.copyOfRange(buffer, 0, headerNameEnd), StandardCharsets.UTF_8),
                        new String(Arrays.copyOfRange(buffer, headerNameEnd+2, byteBufferIdx-2), StandardCharsets.UTF_8))
                }
            }
            byteBufferIdx++;
        }
        throw new Exception("ivalid start line");
    }

    public void parseHttp(InputStream is) throws Exception {
        final Method method = getMethod(is);
        final URI uri = getURI(is);
        final String httpVersion = getHttpVersion(is);

        final byte[] httpHeaderByteBuffer = new byte[MAX_HEADER_LENGTH];
        var httpByteBufferIdx = 0;

//        while (true) {
//            if (httpHeaderByteBuffer[httpByteBufferIdx] == cr) {
//                httpHeaderByteBuffer[httpByteBufferIdx] = (byte) is.read();
//                httpByteBufferIdx++;
//
//                if (httpHeaderByteBuffer[byteBufferIdx] == lf) {
//                    break;
//                }
//            }
//            getHeader();
//            var httpByteBufferIdx = 0;
//        }

        // body

    }

    @Test void test() throws Exception {
        var httpGet = new ByteArrayInputStream("POST / users HTTP/1.1".getBytes(StandardCharsets.UTF_8));
        parseHttp(httpGet);
    }
}
