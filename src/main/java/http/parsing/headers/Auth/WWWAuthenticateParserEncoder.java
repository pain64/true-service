package http.parsing.headers.Auth;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;

import java.util.ArrayList;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class WWWAuthenticateParserEncoder implements ListHeaderParser<Challenge, WWWAuthenticate> {
    @Override
    public WWWAuthenticate create(ArrayList<Challenge> valueArray) {
        return new WWWAuthenticate(valueArray);
    }

    @Override
    public void decode(RequestByteStream bs, Buffer bfr, ArrayList<Challenge> dest) {
        AUTHENTICATE(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, WWWAuthenticate header) {
        BaseEncoder.AUTHENTICATE(rbs, header);
    }
}
