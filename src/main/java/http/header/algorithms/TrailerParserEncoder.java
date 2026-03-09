package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class TrailerParserEncoder implements HeaderParser<Trailer>, HeaderEncoder<Trailer> {
    @Override
    public Trailer PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new Trailer(TOKENS_COMMA_SEPARATED(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(Trailer header) {
        return new byte[0];
    }
}
