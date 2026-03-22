package http.header.algorithms.Auth;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import java.util.ArrayList;

import static http.HttpParser.*;
import static http.BaseDecoder.*;
import static http.header.DTOs.*;

public class AuthenticationInfoParserEncoder implements ValueListHeaderParser<AuthParam, AuthenticationInfo> {
    @Override
    public AuthenticationInfo create(ArrayList<AuthParam> valueArray) {
        return new AuthenticationInfo(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<AuthParam> dest) {
        AUTH_PARAMS(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, AuthenticationInfo header) {
        BaseEncoder.AUTHENTICATION_INFO(rbs, header);
    }
}
