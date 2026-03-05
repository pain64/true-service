package http.header;

import http.Base;
import http.Base.Buffer;
import http.Base.ByteStream;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.ONE_OR_MORE_DIGIT_NUMBER;
import static http.HttpParser.*;
import static http.JumpTables.IS_TCHAR_TABLE;
import static http.header.RangeHeader.*;

public class RangeHeader implements HeaderParser<Range>, HeaderEncoder<Range> {
    public sealed interface RangeSpec  {
        record Start(long value) implements RangeSpec {}
        record Interval(long from, long to) implements RangeSpec {}
        record Suffix(long value) implements RangeSpec {}
    }
    public static class Range extends Header {
        public final RangeUnit rangeUnit;
        public final ArrayList<RangeSpec> value;

        public Range(RangeUnit rangeUnit, ArrayList<RangeSpec> value) {
            this.rangeUnit = rangeUnit;
            this.value = value;
        }
    }

    @Override
    public Range PARSE_HEADER(ByteStream bs, Buffer bfr) {
        if (!TCHAR_CHECK(bs)) throw new RuntimeException("Expected ranges-unit");
        TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
        var rangeUnit = bfr.toStringAndReset();

        CHAR(bs, '=');

        var value = new ArrayList<RangeSpec>();
        RangeSpec rangeSpec;
        do {
            if (CHAR_CHECK(bs, '-')) {
                bs.advance();
                rangeSpec = new RangeSpec.Suffix(ONE_OR_MORE_DIGIT_NUMBER(bs));
            } else {
                var start = ONE_OR_MORE_DIGIT_NUMBER(bs);
                CHAR(bs, '-');

                if (!DIGIT_CHECK(bs)) rangeSpec = new RangeSpec.Start(start);
                else rangeSpec = new RangeSpec.Interval(start, ONE_OR_MORE_DIGIT_NUMBER(bs));
            }

            value.add(rangeSpec);
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));

        return new Range("bytes".equals(rangeUnit) ? new RangeUnit.Bytes() : new RangeUnit.Token(rangeUnit), value);
    }

    @Override
    public byte[] ENCODE_HEADER(Range header) {
        return new byte[0];
    }
}
