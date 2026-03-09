package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ContentLengthParserEncoder implements HeaderParser<ContentLength>, HeaderEncoder<ContentLength> {
    @Override
    public ContentLength PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new ContentLength(ONE_OR_MORE_DIGIT_NUMBER(bs));
    }

    @Override
    public byte[] ENCODE_HEADER(ContentLength header) {
        return new byte[0];
    }

}
