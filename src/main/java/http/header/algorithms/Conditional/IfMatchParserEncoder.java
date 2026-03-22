package http.header.algorithms.Conditional;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class IfMatchParserEncoder implements HeaderParser<IfMatch> {
    @Override
    public IfMatch decode(ByteStream bs, Buffer bfr) {
        return new IfMatch(MATCH_ENTITIES_TAGS(bs, bfr));
    }

    @Override
    public void encode(ResponseByteStream rbs, IfMatch header) {
        BaseEncoder.IF_MATCH(rbs, header);
    }
}
