package http.header;

import http.HttpParser;

import static http.Base.*;
import static http.HttpParser.*;

public class DateHeader implements HeaderParser<DateHeader.Date>, HeaderEncoder<DateHeader.Date> {

    public static class Date extends Header {
        public final String value;

        public Date(String value) {
            this.value = value;
        }
    }

    @Override
    public Date PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new Date(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(Date header) {
        return new byte[0];
    }
}
