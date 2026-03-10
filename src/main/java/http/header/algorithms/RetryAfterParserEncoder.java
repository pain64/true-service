package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class RetryAfterParserEncoder implements HeaderParser<RetryAfter>, HeaderEncoder<RetryAfter> {
    @Override
    public RetryAfter PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new RetryAfter(IS_DIGIT(bs) ?
            new RetryAfterType.DelaySeconds(ONE_OR_MORE_DIGIT_NUMBER(bs)) :
            new RetryAfterType.HttpDate(IMF_FIX_DATE(bs, bfr)));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, RetryAfter header) {
        return new byte[0];
    }
}
