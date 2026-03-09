package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class AuthenticationInfoParserEncoder implements HeaderParser<AuthenticationInfo>, HeaderEncoder<AuthenticationInfo>{
    @Override
    public AuthenticationInfo PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new AuthenticationInfo(AUTH_PARAMS(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(AuthenticationInfo header) {
        return new byte[0];
    }
}
