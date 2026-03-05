package http.header;

import http.HttpParser.Header;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;

import java.util.ArrayList;

import static http.Base.*;
import static http.header.AuthenticationInfoHeader.*;

public class AuthenticationInfoHeader implements HeaderParser<AuthenticationInfo>, HeaderEncoder<AuthenticationInfo> {

    public static class AuthenticationInfo extends Header {
        public final ArrayList<AuthParam> value;

        public AuthenticationInfo(ArrayList<AuthParam> value) {
            this.value = value;
        }
    }

    @Override
    public byte[] ENCODE_HEADER(AuthenticationInfo header) {
        return new byte[0];
    }

    @Override
    public AuthenticationInfo PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new AuthenticationInfo(AUTH_PARAMS(bs, bfr));
    }
}
