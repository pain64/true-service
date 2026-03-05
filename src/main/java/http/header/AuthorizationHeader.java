package http.header;

import http.HttpParser.Header;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.ByteStream;
import static http.header.AuthenticationInfoHeader.*;
import static http.header.AuthorizationHeader.*;

public class AuthorizationHeader implements HeaderParser<Authorization>, HeaderEncoder<Authorization> {

    public static class Authorization extends Header {
        public final String authSchema;
        public final String token;
        public final ArrayList<AuthParam> authPararms;

        public Authorization(String authSchema, String token, ArrayList<AuthParam> authPararms) {
            this.authSchema = authSchema;
            this.token = token;
            this.authPararms = authPararms;
        }
    }

    @Override
    public Authorization PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return AUTHORIZATION(bs, bfr);
    }

    @Override
    public byte[] ENCODE_HEADER(Authorization header) {
        return new byte[0];
    }
}
