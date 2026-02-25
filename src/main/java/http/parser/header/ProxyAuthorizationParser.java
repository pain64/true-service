package http.parser.header;

import http.Base;

import static http.parser.header.AuthorizationParser.*;
import static http.parser.header.AuthorizationParser.AUTHORIZATION;

public class ProxyAuthorizationParser {
    public static class ProxyAuthorization extends Authorization {
        public ProxyAuthorization(Authorization auth) {
            super(auth.authSchema, auth.token, auth.authPararms);
        }
    }

    public static ProxyAuthorization PROXY_AUTHORIZATION(Base.ByteStream bs, Base.Buffer bfr) {
        return new ProxyAuthorization(AUTHORIZATION(bs, bfr));
    }
}
