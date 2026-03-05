package http.header;

import http.Base.Buffer;
import http.Base.ByteStream;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;

import static http.Base.IMF_FIX_DATE;
import static http.header.DateHeader.*;
import static http.header.IfUnmodifiedSinceHeader.*;

public class IfUnmodifiedSinceHeader implements HeaderParser<IfUnmodifiedSince>, HeaderEncoder<IfUnmodifiedSince> {

    public static class IfUnmodifiedSince extends Date {

        public IfUnmodifiedSince(String date) {
            super(date);
        }
    }

    @Override
    public byte[] ENCODE_HEADER(IfUnmodifiedSince header) {
        return new byte[0];
    }

    @Override
    public IfUnmodifiedSince PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new IfUnmodifiedSince(IMF_FIX_DATE(bs, bfr));
    }

}
