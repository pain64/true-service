package http.header.algorithms.Auth;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import tools.jackson.core.ObjectReadContext;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ProxyAuthenticateParserEncoder implements ValueListHeaderParser<Challenge, ProxyAuthenticate> {
    @Override
    public ProxyAuthenticate create(ArrayList<Challenge> valueArray) {
        return new ProxyAuthenticate(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<Challenge> dest) {
        AUTHENTICATE(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, ProxyAuthenticate header) {
        BaseEncoder.AUTHENTICATE(rbs, header);
    }
}
