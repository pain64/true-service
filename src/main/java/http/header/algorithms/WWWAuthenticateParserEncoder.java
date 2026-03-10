package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class WWWAuthenticateParserEncoder implements HeaderParser<WWWAuthenticate>, HeaderEncoder<WWWAuthenticate> {
    @Override
    public WWWAuthenticate PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return null;
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, WWWAuthenticate header) {
        return new byte[0];
    }

}
