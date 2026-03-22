package http.header.algorithms.Conditional;

import http.BaseEncoder;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class IfNoneMatchParserEncoder implements HeaderParser<IfNoneMatch> {
    @Override
    public IfNoneMatch decode(ByteStream bs, Buffer bfr) {
        return new IfNoneMatch(MATCH_ENTITIES_TAGS(bs, bfr));
    }

    @Override
    public void encode(BaseEncoder.ResponseByteStream rbs, IfNoneMatch header) {
        BaseEncoder.IF_MATCH(rbs, header);
    }
}
