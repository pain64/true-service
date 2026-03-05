package http.header;

import http.Base;
import http.Base.Buffer;
import http.Base.ByteStream;
import http.header.AuthenticationInfoHeader.AuthenticationInfo;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.AUTH_PARAMS;
import static http.HttpParser.*;
import static http.header.ProxyAuthenticationInfoHeader.*;

public class ProxyAuthenticationInfoHeader implements HeaderParser<ProxyAuthenticationInfo>, HeaderEncoder<ProxyAuthenticationInfo> {
    public static class ProxyAuthenticationInfo extends AuthenticationInfo {
        public final ArrayList<AuthParam> value;

        public ProxyAuthenticationInfo(ArrayList<AuthParam> value) {
            super(this.value = value);
        }
    }

    @Override
    public ProxyAuthenticationInfo PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new ProxyAuthenticationInfo(AUTH_PARAMS(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(ProxyAuthenticationInfo header) {
        return new byte[0];
    }

}
