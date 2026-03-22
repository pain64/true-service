package http.header.algorithms.Auth;

import http.BaseEncoder;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ProxyAuthenticationInfoParserEncoder implements ValueListHeaderParser<AuthParam, ProxyAuthenticationInfo> {

    @Override
    public ProxyAuthenticationInfo create(ArrayList<AuthParam> valueArray) {
        return new ProxyAuthenticationInfo(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<AuthParam> dest) {
        AUTH_PARAMS(bs, bfr, dest);
    }

    @Override
    public void encode(BaseEncoder.ResponseByteStream rbs, ProxyAuthenticationInfo header) {
        BaseEncoder.AUTHENTICATION_INFO(rbs, header);
    }
}
