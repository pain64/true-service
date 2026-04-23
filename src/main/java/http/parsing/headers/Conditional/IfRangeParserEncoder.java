package http.parsing.headers.Conditional;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class IfRangeParserEncoder implements ValueParser<IfRange> {
    @Override
    public IfRange decode(RequestByteStream rbs, Buffer bfr) {
        if (rbs.current() == '"') {
            return new IfRange(new IfRangeType.EntityTag(ENTITY_TAG(rbs, bfr)));
        }
        else if (rbs.current() != 'W') {
            return new IfRange(new IfRangeType.Date(IMF_FIX_DATE(rbs, bfr)));
        } else {
            if (rbs.lookahead(1) == '/') {
                return new IfRange(new IfRangeType.EntityTag(ENTITY_TAG(rbs, bfr)));
            }
            return new IfRange(new IfRangeType.Date(IMF_FIX_DATE(rbs, bfr)));
        }
    }

    @Override
    public void encode(ResponseByteStream rbs, IfRange header) {
        if (header.value instanceof IfRangeType.EntityTag) BaseEncoder.ENTITY_TAG(rbs, ((IfRangeType.EntityTag) header.value).value);
        else BaseEncoder.IMF_FIX_DATE(rbs, ((IfRangeType.Date) header.value).value);
    }
}
