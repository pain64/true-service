package http.header.algorithms.Auth;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import static http.BaseDecoder.*;
import static http.header.DTOs.*;
import static http.HttpParser.*;

public class AuthorizationParserEncoder implements HeaderParser<Authorization> {
    @Override
    public Authorization decode(ByteStream bs, Buffer bfr) {
        return AUTHORIZATION(bs, bfr);
    }

    @Override
    public void encode(ResponseByteStream rbs, Authorization header) {
        BaseEncoder.AUTHORIZATION(rbs, header);
    }
}
