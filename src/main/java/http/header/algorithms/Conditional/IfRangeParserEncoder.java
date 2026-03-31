package http.header.algorithms.Conditional;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import java.time.LocalDateTime;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class IfRangeParserEncoder implements HeaderParser<IfRange> {
    @Override
    public IfRange decode(ByteStream bs, Buffer bfr) {
        if (bs.current() == '"') {
            return new IfRange(new IfRangeType.EntityTag(ENTITY_TAG_OPT(bs, bfr)));
        }
        else if (bs.current() != 'W') {
            return new IfRange(new IfRangeType.Date(IMF_FIX_DATE(bs, bfr)));
        } else {
            bs.advance();
            if (bs.current() == '/') {
                bs.unadvance((byte) 'W');
                return new IfRange(new IfRangeType.EntityTag(ENTITY_TAG_OPT(bs, bfr)));
            }
            bs.unadvance((byte) 'W');
            return new IfRange(new IfRangeType.Date(IMF_FIX_DATE(bs, bfr)));
        }
    }

    @Override
    public void encode(ResponseByteStream rbs, IfRange header) {
        if (header.value instanceof IfRangeType.EntityTag) BaseEncoder.ENTITY_TAG(rbs, ((IfRangeType.EntityTag) header.value).value);
        else BaseEncoder.IMF_FIX_DATE(rbs, ((IfRangeType.Date) header.value).value);
    }
}
