package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ProxyAuthenticationInfoParserEncoder implements HeaderParser<ProxyAuthenticationInfo>, HeaderEncoder<ProxyAuthenticationInfo> {
    @Override
    public ProxyAuthenticationInfo PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new ProxyAuthenticationInfo(AUTH_PARAMS(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(ProxyAuthenticationInfo header) {
        return new byte[0];
    }

}
