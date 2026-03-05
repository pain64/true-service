package http.header;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.IfMatchHeader.*;

public class IfMatchHeader implements HeaderParser<IfMatch>, HeaderEncoder<IfMatch> {

    public static class IfMatch extends Header {
        public final MatchEntitiesTags value;

        public IfMatch(MatchEntitiesTags value) {
            this.value = value;
        }
    }

    @Override
    public IfMatch PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new IfMatch(MATCH_ENTITIES_TAGS(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(IfMatch header) {
        return new byte[0];
    }

}
