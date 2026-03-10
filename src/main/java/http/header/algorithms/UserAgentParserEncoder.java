package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class UserAgentParserEncoder implements HeaderParser<UserAgent>, HeaderEncoder<UserAgent> {
    @Override
    public UserAgent PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new UserAgent(PRODUCTS(bs, bfr));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, UserAgent header) {
        return new byte[0];
    }
}
