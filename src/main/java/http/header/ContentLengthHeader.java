package http.header;

import http.HttpParser;
import http.HttpParser.Header;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;

import static http.Base.*;
import static http.header.ContentLengthHeader.*;

public class ContentLengthHeader implements HeaderParser<ContentLength>, HeaderEncoder<ContentLength> {

    public static class ContentLength extends Header {
        public final long value;

        public ContentLength(long value) {
            this.value = value;
        }
    }

    @Override
    public byte[] ENCODE_HEADER(ContentLength header) {
        return new byte[0];
    }

    @Override
    public ContentLength PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new ContentLength(ONE_OR_MORE_DIGIT_NUMBER(bs));
    }
}
