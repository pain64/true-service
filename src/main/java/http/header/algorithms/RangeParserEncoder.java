package http.header.algorithms;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import http.header.DTOs;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class RangeParserEncoder implements HeaderParser<Range> {
    @Override
    public Range decode(ByteStream bs, Buffer bfr) {
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
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));

        return new Range("bytes".equals(rangeUnit) ? new RangeUnit.Bytes() : new RangeUnit.Token(rangeUnit), value);
    }

    @Override
    public void encode(ResponseByteStream rbs, Range header) {
        BaseEncoder.RANGE_UNIT(rbs, header.rangeUnit);

        rbs.push('=');

        for (var i = 0; i < header.value.size(); i++) {
            var value = header.value.get(i);

            if (value instanceof RangeSpec.Start) {
                BaseEncoder.NUMBER(rbs, ((RangeSpec.Start) value).value);
                rbs.push('-');
            } else if (value instanceof RangeSpec.Interval) {
                BaseEncoder.NUMBER(rbs, ((RangeSpec.Interval) value).from);
                rbs.push('-');
                BaseEncoder.NUMBER(rbs, ((RangeSpec.Interval) value).to);
            } else {
                rbs.push('-');
                BaseEncoder.NUMBER(rbs, ((RangeSpec.Suffix) value).value);
            }

            if (i != header.value.size()-1) rbs.push(',');
        }
    }
}
