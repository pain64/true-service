package http.parsing.headers.Auth;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class AuthorizationParserEncoder implements ValueParser<Authorization> {
    @Override
    public Authorization decode(RequestByteStream bs, Buffer bfr) {
        return AUTHORIZATION(bs, bfr);
    }

    @Override
    public void encode(ResponseByteStream rbs, Authorization header) {
        BaseEncoder.AUTHORIZATION(rbs, header);
    }
}
