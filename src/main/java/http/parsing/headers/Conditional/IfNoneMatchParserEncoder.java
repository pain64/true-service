package http.parsing.headers.Conditional;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class IfNoneMatchParserEncoder implements ValueParser<IfNoneMatch> {
    @Override
    public IfNoneMatch decode(RequestByteStream bs, Buffer bfr) {
        return new IfNoneMatch(MATCH_ENTITIES_TAGS(bs, bfr));
    }

    @Override
    public void encode(ResponseByteStream rbs, IfNoneMatch header) {
        BaseEncoder.IF_MATCH(rbs, header);
    }
}
