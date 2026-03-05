package http.header;

import static http.Base.*;
import static http.Base.ONE_OR_MORE_DIGIT_NUMBER;
import static http.HttpParser.*;
import static http.JumpTables.IS_TCHAR_TABLE;
import static http.header.ContentRangeHeader.*;

public class ContentRangeHeader implements HeaderParser<ContentRange>, HeaderEncoder<ContentRange> {
    public sealed interface Range {
        record Star() implements Range { }
        record Value(long value) implements Range { }
        record Interval(long from, long to) implements Range { }
    }

    public static class ContentRange extends Header {
        public final RangeUnit rangeUnit;
        public final Range range;
        public final Range size;

        public ContentRange(RangeUnit rangeUnit, Range range, Range size) {
            this.rangeUnit = rangeUnit;
            this.range = range;
            this.size = size;
        }
    }

    @Override
    public ContentRange PARSE_HEADER(ByteStream bs, Buffer bfr) {
        if (!TCHAR_CHECK(bs)) throw new RuntimeException("Expected range-unit");
        TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
        var rangeUnitValue = bfr.toStringAndReset();
        var rangeUnit = "bytes".equals(rangeUnitValue) ? new RangeUnit.Bytes() : new RangeUnit.Token(rangeUnitValue);

        CHAR(bs, ' ');

        Range range;
        Range size;
        if (CHAR_CHECK(bs, '*')) {
            bs.advance(); CHAR(bs, '/');
            range = new Range.Star();
            size = new Range.Value(ONE_OR_MORE_DIGIT_NUMBER(bs));
        } else {
            var from = ONE_OR_MORE_DIGIT_NUMBER(bs);
            CHAR(bs, '-');
            var to = ONE_OR_MORE_DIGIT_NUMBER(bs);
            CHAR(bs, '/');
            range = new Range.Interval(from, to);

            if (CHAR_CHECK(bs, '*')) {bs.advance(); size = new Range.Star();}
            else size = new Range.Value(ONE_OR_MORE_DIGIT_NUMBER(bs));
        }

        return new ContentRange(rangeUnit, range, size);
    }

    @Override
    public byte[] ENCODE_HEADER(ContentRange header) {
        return new byte[0];
    }

}
