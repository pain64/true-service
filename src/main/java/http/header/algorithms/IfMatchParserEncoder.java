package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class IfMatchParserEncoder implements HeaderParser<IfMatch>, HeaderEncoder<IfMatch> {
    @Override
    public IfMatch PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new IfMatch(MATCH_ENTITIES_TAGS(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(IfMatch header) {
        return new byte[0];
    }

}
