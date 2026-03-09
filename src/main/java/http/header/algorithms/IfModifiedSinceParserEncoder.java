package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class IfModifiedSinceParserEncoder implements HeaderParser<IfModifiedSince>, HeaderEncoder<IfModifiedSince> {
    @Override
    public IfModifiedSince PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new IfModifiedSince(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(IfModifiedSince header) {
        return new byte[0];
    }

}
