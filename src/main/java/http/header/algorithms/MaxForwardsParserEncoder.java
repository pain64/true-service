package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class MaxForwardsParserEncoder implements HeaderParser<MaxForwards>, HeaderEncoder<MaxForwards> {
    @Override
    public MaxForwards PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new MaxForwards(ONE_OR_MORE_DIGIT_NUMBER(bs));
    }

    @Override
    public byte[] ENCODE_HEADER(MaxForwards header) {
        return new byte[0];
    }

}
