package http.header.algorithms;

import http.BaseEncoder;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ETagParserEncoder implements HeaderParser<ETag> {
    @Override
    public ETag decode(ByteStream bs, Buffer bfr) {
        var entityTagOpt = ENTITY_TAG_OPT(bs, bfr);
        if (entityTagOpt != null) return new ETag(entityTagOpt);
        throw new HeaderDecodeException(bs.position(), "Expected entity-tag");
    }

    @Override
    public void encode(BaseEncoder.ResponseByteStream rbs, ETag header) {
        BaseEncoder.ENTITY_TAG(rbs, header.value);
    }
}
