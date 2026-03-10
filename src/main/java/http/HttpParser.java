package http;

import com.sun.source.tree.Tree;
import http.BaseEncoder.ResponseByteStream;

import java.util.ArrayList;

import static http.BaseParser.*;


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
    public static class MultiLine<T> extends Header { }

    public interface HeaderParser<T extends Header> {
        public T PARSE_HEADER(ByteStream bs, Buffer bfr);
    }

    public interface HeaderParserMultiline<T> {
        public void PARSE_HEADER(ByteStream bs, Buffer bfr, ArrayList<T> toAdd);
    }

    public interface HeaderEncoder<T extends Header> {
        public void ENCODE_HEADER(ResponseByteStream rbs, T header);
    }

    public interface HeaderEncoderMultiline<T extends Header> {
        public void ENCODE_HEADER(ResponseByteStream rbs, Buffer bfr, T h);
    }



    public static HttpStartLine parseStartLine(ByteStream bs, Buffer bfr, int messageType) {
        // messageType ? start line
        // request: method SP request-target SP HTTP-version
        // response: HTTP-version SP status-code SP [ reason-phrase ]

        return null;
    }

}
