package http.parsing.headers.Conditional;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class IfUnmodifiedSinceParserEncoder implements ValueParser<IfUnmodifiedSince> {
    @Override
    public IfUnmodifiedSince decode(RequestByteStream bs, Buffer bfr) {
        return new IfUnmodifiedSince(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public void encode(ResponseByteStream rbs, IfUnmodifiedSince header) {
        BaseEncoder.IMF_FIX_DATE(rbs, header.value);
    }
}
