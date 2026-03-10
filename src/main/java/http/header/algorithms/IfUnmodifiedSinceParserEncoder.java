package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class IfUnmodifiedSinceParserEncoder implements HeaderParser<IfUnmodifiedSince>, HeaderEncoder<IfUnmodifiedSince> {
    @Override
    public IfUnmodifiedSince PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new IfUnmodifiedSince(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, IfUnmodifiedSince header) {
        return new byte[0];
    }

}
