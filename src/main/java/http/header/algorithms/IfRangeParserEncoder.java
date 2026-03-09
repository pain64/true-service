package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class IfRangeParserEncoder implements HeaderParser<IfRange>, HeaderEncoder<IfRange> {
    @Override
    public IfRange PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var entityTagOpt = ENTITY_TAG_OPT(bs, bfr);
        if (entityTagOpt != null) return new IfRange(new IfRangeType.EntityTag(entityTagOpt));
        return new IfRange(new IfRangeType.Date(IMF_FIX_DATE(bs, bfr)));
    }

    @Override
    public byte[] ENCODE_HEADER(IfRange header) {
        return new byte[0];
    }

}
