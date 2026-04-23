package http.parsing.scalar;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.api.ParseException;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.IS_CHAR;
import static http.parsing.BaseDecoder.IS_DIGIT;
import static http.parsing.api.ParseException.*;

public class LongParserEncoder implements ValueParser<Long> {
    @Override
    public Long decode(RequestByteStream rbs, Buffer bfr) {
        var isNegative = false;

        if(IS_CHAR(rbs, '-')) {isNegative = true; rbs.advance();}

        var i = 0;
        while (IS_DIGIT(rbs) && i < 19) {i++; bfr.push(rbs.advance());}
        if (i == 0) throw new DecodeException(rbs, "No digits found");

        var firstNonZero = false;
        var value = (long) 0;
        var exp = (long) 1;
        try {
            for (var k = bfr.remains() - 1; k >= 0 ; k--) {
                var number = (bfr.bytes[k] - '0');
                value = Math.addExact(value, (bfr.bytes[k] - '0') * exp);

                if (isNegative && number != 0 && !firstNonZero) {value = Math.multiplyExact(value, -1); firstNonZero = true;}
                exp *= 10;
            }
        } catch (ArithmeticException e) {throw new DecodeException(rbs, e);}

        bfr.reset();

        return value;
    }

    @Override
    public void encode(ResponseByteStream rbs, Long header) {

    }
}
