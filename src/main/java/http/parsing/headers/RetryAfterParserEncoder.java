package http.parsing.headers;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class RetryAfterParserEncoder implements ValueParser<RetryAfter> {
    @Override
    public RetryAfter decode(RequestByteStream bs, Buffer bfr) {
        return new RetryAfter(IS_DIGIT(bs) ?
            new RetryAfterType.DelaySeconds(UNSIGNED_LONG(bs)) :
            new RetryAfterType.HttpDate(IMF_FIX_DATE(bs, bfr)));
    }

    @Override
    public void encode(ResponseByteStream rbs, RetryAfter header) {
        if (header.value instanceof RetryAfterType.HttpDate)
            BaseEncoder.IMF_FIX_DATE(rbs, ((RetryAfterType.HttpDate) header.value).value);
        else BaseEncoder.NUMBER(rbs, ((RetryAfterType.DelaySeconds)header.value).value);
    }
}
