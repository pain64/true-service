package http.header;

import http.Base;
import http.Base.EntityTag;
import http.HttpParser.Header;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;

import static http.Base.ENTITY_TAG_OPT;
import static http.header.ETagHeader.*;

public class ETagHeader implements HeaderParser<ETag>, HeaderEncoder<ETag> {

    public static class ETag extends Header {
        public final EntityTag value;

        public ETag(EntityTag value) {
            this.value = value;
        }
    }

    @Override
    public ETag PARSE_HEADER(Base.ByteStream bs, Base.Buffer bfr) {
        var entityTagOpt = ENTITY_TAG_OPT(bs, bfr);
        if (entityTagOpt != null) return new ETag(entityTagOpt);
        throw new RuntimeException("Expected entity-tag");
    }

    @Override
    public byte[] ENCODE_HEADER(ETag header) {
        return new byte[0];
    }
}
