package http.parsing.headers;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import java.util.ArrayList;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class RangeParserEncoder implements ValueParser<Range> {
    @Override
    public Range decode(RequestByteStream bs, Buffer bfr) {
        TOKEN_TCHAR(bs, bfr);
        var rangeUnit = bfr.toStringAndReset();

        CHAR(bs, '=');

        var value = new ArrayList<RangeSpec>();
        RangeSpec rangeSpec;
        do {
            if (IS_CHAR(bs, '-')) {
                bs.advance();
                rangeSpec = new RangeSpec.Suffix(UNSIGNED_LONG(bs));
            } else {
                var start = UNSIGNED_LONG(bs);
                CHAR(bs, '-');

                if (!IS_DIGIT(bs)) rangeSpec = new RangeSpec.Start(start);
                else rangeSpec = new RangeSpec.Interval(start, UNSIGNED_LONG(bs));
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
