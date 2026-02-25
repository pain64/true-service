package http.parser.header;

import http.Base;
import http.parser.header.DateParser.Date;

import static http.parser.header.DateParser.DATE;

public class IfModifiedSinceParser {
    public static class IfModifiedSince extends Date {

        public IfModifiedSince(Date date) {
            super(date.value);
        }
    }

    public static IfModifiedSince IF_MODIFIED_SINCE(Base.ByteStream bs, Base.Buffer bfr) {
        return new IfModifiedSince(DATE(bs, bfr));
    }

}
