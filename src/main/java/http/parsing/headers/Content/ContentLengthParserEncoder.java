package http.parsing.headers.Content;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.dto.Headers.*;
import static http.parsing.BaseDecoder.UNSIGNED_LONG;

public class ContentLengthParserEncoder implements ValueParser<ContentLength> {
    @Override
    public ContentLength decode(RequestByteStream bs, Buffer bfr) {
        return new ContentLength(UNSIGNED_LONG(bs));
    }

    @Override
    public void encode(ResponseByteStream rbs, ContentLength header) {
        BaseEncoder.NUMBER(rbs, header.value);
    }
}
