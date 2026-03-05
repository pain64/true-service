package http.header;

import http.Base;
import http.HttpParser;

import java.util.ArrayList;

import static http.Base.*;

public class ProxyAuthenticateHeader {
    public static class Authorization extends HttpParser.Header {
        public final String authSchema;
        public final String token;
        public final ArrayList<AuthParam> authPararms;

        public Authorization(String authSchema, String token, ArrayList<AuthParam> authPararms) {
            this.authSchema = authSchema;
            this.token = token;
            this.authPararms = authPararms;
        }
    }
}
