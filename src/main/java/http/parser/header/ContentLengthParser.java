package http.parser.header;

import static http.Base.*;

public class ContentLengthParser {
    public static class ContentLength {
        public final long value;

        public ContentLength(long value) {
            this.value = value;
        }
    }

    public static ContentLength CONTENT_LENGTH(ByteStream bs, Buffer bfr) {
        return new ContentLength(AT_LEAST_1_DIGIT_NUMBER(bs));
    }
}
