package http.header;

import http.Base;
import http.Base.MatchEntitiesTags;

import static http.Base.MATCH_ENTITIES_TAGS;
import static http.HttpParser.*;

public class IfNoneMatchHeader implements HeaderParser<IfNoneMatchHeader.IfNoneMatch>, HeaderEncoder<IfNoneMatchHeader.IfNoneMatch> {
    public static class IfNoneMatch extends Header {
        public final MatchEntitiesTags value;

        public IfNoneMatch(MatchEntitiesTags value) {
            this.value = value;
        }
    }

    @Override
    public IfNoneMatch PARSE_HEADER(Base.ByteStream bs, Base.Buffer bfr) {
        return new IfNoneMatch(MATCH_ENTITIES_TAGS(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(IfNoneMatch header) {
        return new byte[0];
    }

}
