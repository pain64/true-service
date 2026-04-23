package http.parsing.headers.Content;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class ContentRangeParserEncoder implements ValueParser<ContentRange> {
    @Override
    public ContentRange decode(RequestByteStream bs, Buffer bfr) {
        CHAR(bs,'b'); CHAR(bs,'y'); CHAR(bs,'t'); CHAR(bs,'e'); CHAR(bs,'s');
//        TOKEN_TCHAR(bs, bfr);
//        var rangeUnitValue = bfr.toStringAndReset();
//        var rangeUnit = "bytes".equals(rangeUnitValue) ? new RangeUnit.Bytes() : new RangeUnit.Token(rangeUnitValue);
        var rangeUnit = new RangeUnit.Bytes();

        CHAR(bs, ' ');

        ContentRangeType range;
        ContentRangeType size;
        if (IS_CHAR(bs, '*')) {
            bs.advance(); CHAR(bs, '/');
            range = new ContentRangeType.Star();
            size = new ContentRangeType.Value(UNSIGNED_LONG(bs));
        } else {
            var from = UNSIGNED_LONG(bs);
            CHAR(bs, '-');
            var to = UNSIGNED_LONG(bs);
            CHAR(bs, '/');
            range = new ContentRangeType.Interval(from, to);

            if (IS_CHAR(bs, '*')) {bs.advance(); size = new ContentRangeType.Star();}
            else size = new ContentRangeType.Value(UNSIGNED_LONG(bs));
        }

        return new ContentRange(rangeUnit, range, size);
    }

    public static void CONTENT_RANGE_TYPE(ResponseByteStream rbs, ContentRangeType crt) {
        if (crt instanceof ContentRangeType.Star) rbs.push('*');
        else if (crt instanceof ContentRangeType.Value) BaseEncoder.NUMBER(rbs, ((ContentRangeType.Value) crt).value);
        else {
            BaseEncoder.NUMBER(rbs, ((ContentRangeType.Interval) crt).from);
            rbs.push('-');
            BaseEncoder.NUMBER(rbs, ((ContentRangeType.Interval) crt).to);
        }
    }

    @Override
    public void encode(ResponseByteStream rbs, ContentRange header) {
        BaseEncoder.RANGE_UNIT(rbs, header.rangeUnit);

        rbs.push(' ');

        CONTENT_RANGE_TYPE(rbs, header.range);
        rbs.push('/');
        CONTENT_RANGE_TYPE(rbs, header.size);
    }
}
