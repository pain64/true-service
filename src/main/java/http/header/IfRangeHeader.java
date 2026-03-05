package http.header;

import http.Base;
import http.Base.Buffer;
import http.Base.ByteStream;
import http.Base.IfRangeType;

import static http.Base.ENTITY_TAG_OPT;
import static http.Base.IMF_FIX_DATE;
import static http.HttpParser.*;
import static http.header.IfRangeHeader.*;

public class IfRangeHeader implements HeaderParser<IfRange>, HeaderEncoder<IfRange> {

    public static class IfRange extends Header {
        public final IfRangeType value;

        public IfRange(IfRangeType value) {
            this.value = value;
        }
    }

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
