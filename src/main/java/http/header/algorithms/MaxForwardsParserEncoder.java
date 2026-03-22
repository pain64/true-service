package http.header.algorithms;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class MaxForwardsParserEncoder implements HeaderParser<MaxForwards> {
    @Override
    public MaxForwards decode(ByteStream bs, Buffer bfr) {
        return new MaxForwards(ONE_OR_MORE_DIGIT_NUMBER(bs));
    }

    @Override
    public void encode(ResponseByteStream rbs, MaxForwards header) {
        BaseEncoder.NUMBER(rbs, header.value);
    }
}
