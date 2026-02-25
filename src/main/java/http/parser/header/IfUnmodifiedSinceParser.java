package http.parser.header;

import http.Base;
import http.Base.Buffer;
import http.Base.ByteStream;

import static http.parser.header.DateParser.*;
import static http.parser.header.DateParser.DATE;

public class IfUnmodifiedSinceParser {
    public static class IfUnmodifiedSince extends Date {

        public IfUnmodifiedSince(Date date) {
            super(date.value);
        }
    }

    public static IfUnmodifiedSince IF_UNMODIFIED_SINCE(ByteStream bs, Buffer bfr) {
        return new IfUnmodifiedSince(DATE(bs, bfr));
    }

}
