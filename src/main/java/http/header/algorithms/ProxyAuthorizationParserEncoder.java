package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ProxyAuthorizationParserEncoder implements HeaderParser<ProxyAuthorization>, HeaderEncoder<ProxyAuthorization> {
    @Override
    public ProxyAuthorization PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new ProxyAuthorization(AUTHORIZATION(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(ProxyAuthorization header) {
        return new byte[0];
    }

}
