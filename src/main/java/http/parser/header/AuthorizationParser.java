package http.parser.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.ByteStream;
import static http.Base.SKIP_OWS;
import static http.JumpTables.*;
import static http.parser.header.AuthenticationInfoParser.AUTH_PARAM;

public class AuthorizationParser {

    public static class Authorization {
        public final String authSchema;
        public final String token;
        public final ArrayList<AuthenticationInfoParser.AuthParam> authPararms;

        public Authorization(String authSchema, String token, ArrayList<AuthenticationInfoParser.AuthParam> authPararms) {
            this.authSchema = authSchema;
            this.token = token;
            this.authPararms = authPararms;
        }
    }
    // maybe 0
    // no -1
    public static int TOKEN68(ByteStream bs, Buffer bfr) {
        bfr.reset();
        byte b;
        var requiresEqual = false;

        while (true) {
            if ((b = TOKEN68_OPT(bs)) == -1) return 0;
            if (b == '=') requiresEqual = true;
            if (b != '=' && requiresEqual) return -1;

            bfr.push(b);
        }
    }

    public static Authorization AUTHORIZATION(ByteStream bs, Buffer bfr) {
        bfr.reset();

        byte b;
        if ((b = TCHAR_OPT(bs)) == -1) throw new RuntimeException("Expected auth schema");
        bs.unadvance(b);

        TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);

        var authSchema = bfr.toStringAndReset();

        if (CHAR_OPT(bs, ' ') == -1)
            return new Authorization(authSchema, null, null);

        SKIP_OWS(bs);

        if (TOKEN68(bs, bfr) == 0 && (b = CHAR_OPT(bs, '\r')) != -1) {
            bs.unadvance((byte) '\r');
            return new Authorization(authSchema, bfr.toStringAndReset(), null);
        }

        for (var i = bfr.remains() - 1; i >= 0; i--) bs.unadvance(bfr.bytes[i]);
        bfr.reset();

        var authParam = AUTH_PARAM(bs, bfr);

        return new Authorization(authSchema, null, authParam);
    }
}
