package http.parsing.headers;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class MaxForwardsParserEncoder implements ValueParser<MaxForwards> {
    @Override
    public MaxForwards decode(RequestByteStream bs, Buffer bfr) {
        return new MaxForwards(UNSIGNED_LONG(bs));
    }

    @Override
    public void encode(ResponseByteStream rbs, MaxForwards header) {
        BaseEncoder.NUMBER(rbs, header.value);
    }
}
