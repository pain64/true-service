package http.header.algorithms.Content;

import http.BaseEncoder;
import tools.jackson.core.ObjectReadContext;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ContentLengthParserEncoder implements HeaderParser<ContentLength> {
    @Override
    public ContentLength decode(ByteStream bs, Buffer bfr) {
        return new ContentLength(ONE_OR_MORE_DIGIT_NUMBER(bs));
    }

    @Override
    public void encode(BaseEncoder.ResponseByteStream rbs, ContentLength header) {
        BaseEncoder.NUMBER(rbs, header.value);
    }
}
