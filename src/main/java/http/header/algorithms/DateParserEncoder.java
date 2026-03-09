package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class DateParserEncoder implements HeaderParser<Date>, HeaderEncoder<Date> {
    @Override
    public Date PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new Date(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(Date header) {
        return new byte[0];
    }
}
