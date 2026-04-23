package http.parsing.headers.Auth;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;

import java.util.ArrayList;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class ProxyAuthenticationInfoParserEncoder implements ListHeaderParser<AuthParam, ProxyAuthenticationInfo> {

    @Override
    public ProxyAuthenticationInfo create(ArrayList<AuthParam> valueArray) {
        return new ProxyAuthenticationInfo(valueArray);
    }

    @Override
    public void decode(RequestByteStream bs, Buffer bfr, ArrayList<AuthParam> dest) {
        AUTH_PARAMS(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, ProxyAuthenticationInfo header) {
        BaseEncoder.AUTHENTICATION_INFO(rbs, header);
    }
}
