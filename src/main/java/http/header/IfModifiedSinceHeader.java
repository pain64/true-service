package http.header;

import http.Base;
import http.HttpParser;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;
import http.header.DateHeader.Date;

import static http.Base.IMF_FIX_DATE;
import static http.header.IfModifiedSinceHeader.*;

public class IfModifiedSinceHeader implements HeaderParser<IfModifiedSince>, HeaderEncoder<IfModifiedSince> {

    public static class IfModifiedSince extends Date {

        public IfModifiedSince(String date) {
            super(date);
        }
    }

    @Override
    public IfModifiedSince PARSE_HEADER(Base.ByteStream bs, Base.Buffer bfr) {
        return new IfModifiedSince(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(IfModifiedSince header) {
        return new byte[0];
    }

}
