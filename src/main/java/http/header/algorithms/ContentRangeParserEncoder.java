package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ContentRangeParserEncoder implements HeaderParser<ContentRange>, HeaderEncoder<ContentRange> {
    @Override
    public ContentRange PARSE_HEADER(ByteStream bs, Buffer bfr) {
        TOKEN_TCHAR(bs, bfr);
        var rangeUnitValue = bfr.toStringAndReset();
        var rangeUnit = "bytes".equals(rangeUnitValue) ? new RangeUnit.Bytes() : new RangeUnit.Token(rangeUnitValue);

        CHAR(bs, ' ');

        ContentRangeType range;
        ContentRangeType size;
        if (IS_CHAR(bs, '*')) {
            bs.advance(); CHAR(bs, '/');
            range = new ContentRangeType.Star();
            size = new ContentRangeType.Value(ONE_OR_MORE_DIGIT_NUMBER(bs));
        } else {
            var from = ONE_OR_MORE_DIGIT_NUMBER(bs);
            CHAR(bs, '-');
            var to = ONE_OR_MORE_DIGIT_NUMBER(bs);
            CHAR(bs, '/');
            range = new ContentRangeType.Interval(from, to);

            if (IS_CHAR(bs, '*')) {bs.advance(); size = new ContentRangeType.Star();}
            else size = new ContentRangeType.Value(ONE_OR_MORE_DIGIT_NUMBER(bs));
        }

        return new ContentRange(rangeUnit, range, size);
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, ContentRange header) {
        return new byte[0];
    }

}
