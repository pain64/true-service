package http.parsing.headers.Auth;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class ProxyAuthorizationParserEncoder implements ValueParser<ProxyAuthorization> {
    @Override
    public ProxyAuthorization decode(RequestByteStream bs, Buffer bfr) {
        return new ProxyAuthorization(AUTHORIZATION(bs, bfr));
    }

    @Override
    public void encode(ResponseByteStream rbs, ProxyAuthorization header) {
        BaseEncoder.AUTHORIZATION(rbs, header);
    }
}
