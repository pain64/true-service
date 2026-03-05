package http;

import com.sun.source.tree.Tree;

import java.util.ArrayList;

import static http.Base.*;


public class HttpParser {
    public static final int REQUEST = 0;
    public static final int RESPONSE = 1;
    public final Tree<byte[]> allowedRequestTargetsPrefix;

    public HttpParser(Tree<byte[]> allowedRequestTargetsPrefix) {
        this.allowedRequestTargetsPrefix = allowedRequestTargetsPrefix;
    }

    public sealed interface HttpStartLine {
        final class Request implements HttpStartLine {
            public final Method METHOD;
            public final String TARGET;
            public final String HTTP_VERSION;


            public Request(Method method, String target, String httpVersion) {
                METHOD = method;
                TARGET = target;
                HTTP_VERSION = httpVersion;
            }
        }
        final class Response implements HttpStartLine {
            public final String HTTP_VERSION;
            public final int STATUS_CODE;
            public final String REASON_PHRASE;

            public Response(String httpVersion, int statusCode, String reasonPhrase) {
                HTTP_VERSION = httpVersion;
                STATUS_CODE = statusCode;
                REASON_PHRASE = reasonPhrase;
            }
        }
    }

    public static class Header { }

    public interface HeaderParser<T extends Header> {
        public T PARSE_HEADER(ByteStream bs, Buffer bfr);
    }

    public interface HeaderEncoder<T extends Header> {
        public byte[] ENCODE_HEADER(T header);
    }

    public abstract static class HeaderWithParser implements HeaderParser {
        public final byte[] name;

        protected HeaderWithParser(byte[] name) {
            this.name = name;
        }
    }

    public static HttpStartLine parseStartLine(ByteStream bs, Buffer bfr, int messageType) {
        // messageType ? start line
        // request: method SP request-target SP HTTP-version
        // response: HTTP-version SP status-code SP [ reason-phrase ]

        return null;
    }

    public static ArrayList<Header> parseHeaders(ByteStream bs, Buffer bfr, ArrayList<HeaderWithParser> headerWithParsers) {
        // check for all required headers
        return null;
    }

}
