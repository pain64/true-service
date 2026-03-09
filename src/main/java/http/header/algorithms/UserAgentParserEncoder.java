package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class UserAgentParserEncoder implements HeaderParser<UserAgent>, HeaderEncoder<UserAgent> {
    @Override
    public UserAgent PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new UserAgent(PRODUCTS(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(UserAgent header) {
        return new byte[0];
    }
}
