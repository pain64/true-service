package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class IfNoneMatchParserEncoder implements HeaderParser<IfNoneMatch>, HeaderEncoder<IfNoneMatch> {
    @Override
    public IfNoneMatch PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new IfNoneMatch(MATCH_ENTITIES_TAGS(bs, bfr));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, IfNoneMatch header) {
        return new byte[0];
    }

}
