package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ContentLengthParserEncoder implements HeaderParser<ContentLength>, HeaderEncoder<ContentLength> {
    @Override
    public ContentLength PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new ContentLength(ONE_OR_MORE_DIGIT_NUMBER(bs));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, ContentLength header) {
        return new byte[0];
    }

}
