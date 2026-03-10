package http.header.algorithms;

import static http.BaseParser.*;
import static http.header.DTOs.*;
import static http.HttpParser.*;

public class AuthorizationParserEncoder implements HeaderParser<Authorization>, HeaderEncoder<Authorization> {
    @Override
    public Authorization PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return AUTHORIZATION(bs, bfr);
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, Authorization header) {
        return new byte[0];
    }
}
