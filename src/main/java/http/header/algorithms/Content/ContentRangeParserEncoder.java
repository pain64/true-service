package http.header.algorithms.Content;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import http.header.DTOs;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ContentRangeParserEncoder implements HeaderParser<ContentRange> {
    @Override
    public ContentRange decode(ByteStream bs, Buffer bfr) {
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
