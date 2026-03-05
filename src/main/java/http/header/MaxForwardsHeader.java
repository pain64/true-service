package http.header;

import http.Base;
import http.HttpParser;

import static http.Base.ONE_OR_MORE_DIGIT_NUMBER;
import static http.HttpParser.*;
import static http.header.MaxForwardsHeader.*;

public class MaxForwardsHeader implements HeaderParser<MaxForwards>, HeaderEncoder<MaxForwards> {

    public static class MaxForwards extends Header {
        public final long value;

        public MaxForwards(long value) {
            this.value = value;
        }
    }

    @Override
    public MaxForwards PARSE_HEADER(Base.ByteStream bs, Base.Buffer bfr) {
        return new MaxForwards(ONE_OR_MORE_DIGIT_NUMBER(bs));
    }

    @Override
    public byte[] ENCODE_HEADER(MaxForwards header) {
        return new byte[0];
    }

}
