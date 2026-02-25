package http.parser.header;

import http.Base;

import java.util.ArrayList;

import static http.Base.TOKENS_COMMA_SEPARATED;

public class ContentEncodingParser {
    public static class ContentEncoding {
        public final ArrayList<String> value;

        public ContentEncoding(ArrayList<String> value) {
            this.value = value;
        }
    }

    public static ContentEncoding CONTENT_ENCODING(Base.ByteStream bs, Base.Buffer bfr) {
        return new ContentEncoding(TOKENS_COMMA_SEPARATED(bs, bfr));
    }
}
