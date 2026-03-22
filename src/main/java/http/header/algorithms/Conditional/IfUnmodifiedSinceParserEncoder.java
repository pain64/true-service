package http.header.algorithms.Conditional;

import http.BaseEncoder;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class IfUnmodifiedSinceParserEncoder implements HeaderParser<IfUnmodifiedSince> {
    @Override
    public IfUnmodifiedSince decode(ByteStream bs, Buffer bfr) {
        return new IfUnmodifiedSince(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public void encode(BaseEncoder.ResponseByteStream rbs, IfUnmodifiedSince header) {
        BaseEncoder.IMF_FIX_DATE(rbs, header.value);
    }
}
