package http.header.algorithms.Auth;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class WWWAuthenticateParserEncoder implements ValueListHeaderParser<Challenge, WWWAuthenticate> {
    @Override
    public WWWAuthenticate create(ArrayList<Challenge> valueArray) {
        return new WWWAuthenticate(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<Challenge> dest) {
        AUTHENTICATE(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, WWWAuthenticate header) {
        BaseEncoder.AUTHENTICATE(rbs, header);
    }
}
