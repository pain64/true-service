package http.header.algorithms;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import http.header.DTOs;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class RetryAfterParserEncoder implements HeaderParser<RetryAfter> {
    @Override
    public RetryAfter decode(ByteStream bs, Buffer bfr) {
        return new RetryAfter(IS_DIGIT(bs) ?
            new RetryAfterType.DelaySeconds(ONE_OR_MORE_DIGIT_NUMBER(bs)) :
            new RetryAfterType.HttpDate(IMF_FIX_DATE(bs, bfr)));
    }

    @Override
    public void encode(ResponseByteStream rbs, RetryAfter header) {
        if (header.value instanceof RetryAfterType.HttpDate)
            BaseEncoder.IMF_FIX_DATE(rbs, ((RetryAfterType.HttpDate) header.value).value);
        else BaseEncoder.NUMBER(rbs, ((RetryAfterType.DelaySeconds)header.value).value);
    }
}
