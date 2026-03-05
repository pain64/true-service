package http.header;

import http.Base;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;

import static http.Base.AUTHORIZATION;
import static http.header.AuthorizationHeader.*;
import static http.header.ProxyAuthorizationHeader.*;

public class ProxyAuthorizationHeader implements HeaderParser<ProxyAuthorization>, HeaderEncoder<ProxyAuthorization> {

    public static class ProxyAuthorization extends Authorization {
        public ProxyAuthorization(Authorization auth) {
            super(auth.authSchema, auth.token, auth.authPararms);
        }
    }

    @Override
    public ProxyAuthorization PARSE_HEADER(Base.ByteStream bs, Base.Buffer bfr) {
        return new ProxyAuthorization(AUTHORIZATION(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(ProxyAuthorization header) {
        return new byte[0];
    }

}
