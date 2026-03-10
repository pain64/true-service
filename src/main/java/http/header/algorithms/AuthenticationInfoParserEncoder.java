package http.header.algorithms;

import java.util.ArrayList;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class AuthenticationInfoParserEncoder implements HeaderParserMultiline<AuthParam>, HeaderEncoder<AuthenticationInfo>{
    @Override
    public void PARSE_HEADER(ByteStream bs, Buffer bfr, ArrayList<AuthParam> toAdd) {
        toAdd.addAll(AUTH_PARAMS(bs, bfr));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, AuthenticationInfo header) {
        return new byte[0];
    }

}
