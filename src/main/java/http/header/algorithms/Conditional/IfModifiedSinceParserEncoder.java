package http.header.algorithms.Conditional;

import http.BaseEncoder;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class IfModifiedSinceParserEncoder implements HeaderParser<IfModifiedSince> {
    @Override
    public IfModifiedSince decode(ByteStream bs, Buffer bfr) {
        return new IfModifiedSince(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public void encode(BaseEncoder.ResponseByteStream rbs, IfModifiedSince header) {
        BaseEncoder.IMF_FIX_DATE(rbs, header.value);
    }
}
