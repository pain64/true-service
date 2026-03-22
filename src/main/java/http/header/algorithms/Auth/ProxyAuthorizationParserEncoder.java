package http.header.algorithms.Auth;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ProxyAuthorizationParserEncoder implements HeaderParser<ProxyAuthorization> {
    @Override
    public ProxyAuthorization decode(ByteStream bs, Buffer bfr) {
        return new ProxyAuthorization(AUTHORIZATION(bs, bfr));
    }

    @Override
    public void encode(ResponseByteStream rbs, ProxyAuthorization header) {
        BaseEncoder.AUTHORIZATION(rbs, header);
    }
}
