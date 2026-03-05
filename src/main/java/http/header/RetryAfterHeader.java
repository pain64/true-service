package http.header;

import http.Base.Buffer;
import http.Base.ByteStream;
import http.HttpParser;
import http.HttpParser.Header;

import static http.Base.*;

public class RetryAfterHeader implements HttpParser.HeaderParser<RetryAfterHeader.RetryAfter>, HttpParser.HeaderEncoder<RetryAfterHeader.RetryAfter> {
    public sealed interface RetryAfterType {
        record HttpDate(String value) implements RetryAfterType{}
        record DelaySeconds(long value) implements RetryAfterType {}
    }
    public static class RetryAfter extends Header {
        public final RetryAfterType value;

        public RetryAfter(RetryAfterType value) {
            this.value = value;
        }
    }

    @Override
    public RetryAfter PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new RetryAfter(DIGIT_CHECK(bs) ?
            new RetryAfterType.DelaySeconds(ONE_OR_MORE_DIGIT_NUMBER(bs)) :
            new RetryAfterType.HttpDate(IMF_FIX_DATE(bs, bfr)));
    }

    @Override
    public byte[] ENCODE_HEADER(RetryAfter header) {
        return new byte[0];
    }
}
