package http.header.algorithms;

import static http.Base.*;
import static http.header.DTOs.*;
import static http.HttpParser.*;

public class AuthorizationParserEncoder implements HeaderParser<Authorization>, HeaderEncoder<Authorization> {
    @Override
    public Authorization PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return AUTHORIZATION(bs, bfr);
    }

    @Override
    public byte[] ENCODE_HEADER(Authorization header) {
        return new byte[0];
    }
}
