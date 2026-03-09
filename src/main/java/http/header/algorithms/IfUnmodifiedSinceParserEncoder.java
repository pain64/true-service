package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class IfUnmodifiedSinceParserEncoder implements HeaderParser<IfUnmodifiedSince>, HeaderEncoder<IfUnmodifiedSince> {
    @Override
    public IfUnmodifiedSince PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new IfUnmodifiedSince(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(IfUnmodifiedSince header) {
        return new byte[0];
    }

}
