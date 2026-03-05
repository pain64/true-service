package http.header;

import http.Base;
import http.HttpParser.Header;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;

import java.util.ArrayList;

import static http.Base.TOKENS_COMMA_SEPARATED;
import static http.header.ContentEncodingHeader.*;

public class ContentEncodingHeader implements HeaderParser<ContentEncoding>, HeaderEncoder<ContentEncoding> {

    public static class ContentEncoding extends Header {
        public final ArrayList<String> value;

        public ContentEncoding(ArrayList<String> value) {
            this.value = value;
        }
    }

    @Override
    public ContentEncoding PARSE_HEADER(Base.ByteStream bs, Base.Buffer bfr) {
        return new ContentEncoding(TOKENS_COMMA_SEPARATED(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(ContentEncoding header) {
        return new byte[0];
    }

}
