package http.parsing.headers;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ParseException;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class ETagParserEncoder implements ValueParser<ETag> {
    @Override
    public ETag decode(RequestByteStream rbs, Buffer bfr) {
        if (IS_ENTITY_TAG(rbs)) return new ETag(ENTITY_TAG(rbs, bfr));
        throw new ParseException.DecodeException(rbs, "Expected entity-tag");
    }

    @Override
    public void encode(ResponseByteStream rbs, ETag header) {
        BaseEncoder.ENTITY_TAG(rbs, header.value);
    }
}
