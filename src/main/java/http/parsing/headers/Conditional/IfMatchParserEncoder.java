package http.parsing.headers.Conditional;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class IfMatchParserEncoder implements ValueParser<IfMatch> {
    @Override
    public IfMatch decode(RequestByteStream bs, Buffer bfr) {
        return new IfMatch(MATCH_ENTITIES_TAGS(bs, bfr));
    }

    @Override
    public void encode(ResponseByteStream rbs, IfMatch header) {
        BaseEncoder.IF_MATCH(rbs, header);
    }
}
