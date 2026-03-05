package http.header;

import http.Base;
import http.HttpParser;

import static http.Base.IMF_FIX_DATE;
import static http.header.DateHeader.*;

public class LastModifiedHeader implements HttpParser.HeaderParser<LastModifiedHeader.LastModified>, HttpParser.HeaderEncoder<LastModifiedHeader.LastModified> {

    public static class LastModified extends Date {

        public LastModified(String date) {
            super(date);
        }
    }

    @Override
    public LastModified PARSE_HEADER(Base.ByteStream bs, Base.Buffer bfr) {
        return new LastModified(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(LastModified header) {
        return new byte[0];
    }

}
