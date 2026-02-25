package http.parser.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.BYTE_OPT;
import static http.Base.Buffer;
import static http.Base.ByteStream;
import static http.Base.SKIP_OWS;

public class AuthenticationInfoParser {

    public sealed interface AuthParam {
        record Token(String name, String value) implements AuthParam {}
    }

    public static class AuthenticationInfo {
        public final ArrayList<AuthParam> value;

        public AuthenticationInfo(ArrayList<AuthParam> value) {
            this.value = value;
        }
    }

    public static ArrayList<AuthParam> AUTH_PARAM(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<AuthParam>();
        bfr.reset();

        byte b;

        while (true) {

            if ((b = TCHAR_OPT(bs)) == -1) break;
            else bs.unadvance(b);

            TOKEN_OPT(bs, bfr);
            var tokenName = bfr.toStringAndReset();

            SKIP_OWS(bs);
            BYTE(bs, '=');
            SKIP_OWS(bs);

            if ((b = TCHAR_DQUOTE_OPT(bs)) == -1)
                throw new RuntimeException("Expected TCHAR or \"");
            bs.unadvance(b);

            String tokenValue;

            TOKEN_OR_QUOTED_STRING(bs, bfr);

            tokenValue = bfr.toStringAndReset();

            value.add(new AuthParam.Token(tokenName, tokenValue));

            SKIP_OWS(bs);
            if (BYTE_OPT(bs, ',') != -1) break;
            SKIP_OWS(bs);
        }
        return value;
    }

    public static AuthenticationInfo AUTHENTICATION_INFO(ByteStream bs, Buffer bfr) {
        return new AuthenticationInfo(AUTH_PARAM(bs, bfr));
    }
}
