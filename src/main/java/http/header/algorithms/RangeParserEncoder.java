package http.header.algorithms;

import java.util.ArrayList;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class RangeParserEncoder implements HeaderParser<Range>, HeaderEncoder<Range> {
    @Override
    public Range PARSE_HEADER(ByteStream bs, Buffer bfr) {
        TOKEN_TCHAR(bs, bfr);
        var rangeUnit = bfr.toStringAndReset();

        CHAR(bs, '=');

        var value = new ArrayList<RangeSpec>();
        RangeSpec rangeSpec;
        do {
            if (IS_CHAR(bs, '-')) {
                bs.advance();
                rangeSpec = new RangeSpec.Suffix(ONE_OR_MORE_DIGIT_NUMBER(bs));
            } else {
                var start = ONE_OR_MORE_DIGIT_NUMBER(bs);
                CHAR(bs, '-');

                if (!IS_DIGIT(bs)) rangeSpec = new RangeSpec.Start(start);
                else rangeSpec = new RangeSpec.Interval(start, ONE_OR_MORE_DIGIT_NUMBER(bs));
            }

            value.add(rangeSpec);
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));

        return new Range("bytes".equals(rangeUnit) ? new RangeUnit.Bytes() : new RangeUnit.Token(rangeUnit), value);
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, Range header) {
        return new byte[0];
    }
}
