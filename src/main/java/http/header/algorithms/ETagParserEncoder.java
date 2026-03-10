package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ETagParserEncoder implements HeaderParser<ETag>, HeaderEncoder<ETag> {
    @Override
    public ETag PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var entityTagOpt = ENTITY_TAG_OPT(bs, bfr);
        if (entityTagOpt != null) return new ETag(entityTagOpt);
        throw new RuntimeException("Expected entity-tag");
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, ETag header) {
        return new byte[0];
    }
}
