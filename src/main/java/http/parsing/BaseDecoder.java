package http.parsing;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import http.RequestByteStream;
import http.Buffer;
import http.parsing.api.ParseException.DecodeException;

import static http.parsing.JumpTables.*;
import static http.dto.Headers.*;

public class BaseDecoder {

    public static void CHAR(RequestByteStream rbs, char ch) {
        byte b = rbs.current();
        if (b != ch) throw new DecodeException(rbs, "Expected " + ch);
        rbs.advance();
    }

    public static boolean IS_CHAR(RequestByteStream rbs, char ch) {
        return rbs.current() == ch;
    }

    public static byte DIGIT(RequestByteStream rbs) {
        var b = rbs.current(); if (b >= '0' && b <= '9') { rbs.advance(); return b; }
        throw new DecodeException(rbs, "Expected DIGIT");
    }

    public static boolean IS_DIGIT(RequestByteStream rbs) {
        var b = rbs.current();
        return (b >= '0' && b <= '9');
    }

    public static boolean IS_DIGIT(byte b) {
        return (b >= '0' && b <= '9');
    }

    public static boolean IS_ALPHA(RequestByteStream rbs) {
        return IS_ALPHA_TABLE[rbs.current()];
    }

    public static byte HEXDIG(RequestByteStream rbs) {
        byte b = rbs.current(); if (IS_HEXDIG_TABLE[b]) {rbs.advance(); return b;}
        throw new DecodeException(rbs, "Expected HEXDIG");
    }

    public static boolean IS_TCHAR(RequestByteStream rbs) {
        return IS_TCHAR_TABLE[rbs.current()];
    }

    public static boolean IS_QDTEXT(RequestByteStream rbs) {
        return IS_QDTEXT_TABLE[rbs.current()];
    }

    public static boolean IS_QUOTED_PAIR(RequestByteStream rbs) {
        return IS_QUOTED_PAIR_TABLE[rbs.current()];
    }

    public static void CRLF(RequestByteStream rbs) {
        CHAR(rbs, '\r'); CHAR(rbs, '\n');
    }

    public static void SKIP_TO_CRLF(RequestByteStream rbs) {
        while (!IS_CHAR(rbs, '\r')) {
            rbs.advance();
        }
        CRLF(rbs);
    }

    public static void SKIP_TO_SP(RequestByteStream rbs) {
        while (!IS_CHAR(rbs, ' ')) {
            rbs.advance();
        }
        CRLF(rbs);
    }

    public static boolean SEMICOLON_OR_CR_SKIP(RequestByteStream rbs) {
        while (rbs.current() != ';' || rbs.current() != '\r') {
            rbs.advance();
        }
        var isSemicolon = (rbs.current() == ';');
        if (isSemicolon) rbs.advance();

        return isSemicolon;
    }

    public static boolean AMPERSAND_SKIP(RequestByteStream rbs) {
        var isAmpersand = IS_CHAR(rbs, '&');
        if (isAmpersand) rbs.advance();

        return isAmpersand;
    }

    private static void TOKEN(RequestByteStream rbs, Buffer bfr, boolean[] IS_TOKEN_CHAR_TABLE) {
        var b = rbs.current();
        if (!IS_TOKEN_CHAR_TABLE[b]) throw new DecodeException(rbs, "Expected token");

        do {
            bfr.push(rbs.advance());
        } while (IS_TOKEN_CHAR_TABLE[rbs.current()]);
    }

    public static void TOKEN_TCHAR(RequestByteStream rbs, Buffer bfr) {
        TOKEN(rbs, bfr, IS_TCHAR_TABLE);
    }

    public static byte hexdigToByte(byte h) {
        if (h < 'A') return (byte) (h - '0');
        else return (byte) (10 + (h - 'A'));
    }


    public static void TOKEN_PERCENT_ENCODED(RequestByteStream rbs, Buffer bfr) {
        while (IS_PERCENT_ENCODING_TABLE[rbs.current()]) {
            var b = rbs.advance();
            if (b == '%') {
                var h1 = HEXDIG(rbs);
                var h2 = HEXDIG(rbs);
                bfr.push((byte) (hexdigToByte(h1) * 16 + hexdigToByte(h2)));
            } else bfr.push(b);
        }
    }

    public static void TOKEN_COOKIE(RequestByteStream rbs, Buffer bfr) {
        TOKEN(rbs, bfr, IS_COOKIE_TABLE);
    }

    public static void TOKEN_ALPHA(RequestByteStream rbs, Buffer bfr) {
        TOKEN(rbs, bfr, IS_ALPHA_TABLE);
    }

    public static void TOKEN_ALPHA_OR_DIGIT(RequestByteStream rbs, Buffer bfr) {
        TOKEN(rbs, bfr, IS_ALPHA_OR_DIGIT_TABLE);
    }

    public static float WEIGHT(RequestByteStream rbs, Buffer bfr) {
        CHAR(rbs, 'q'); CHAR(rbs, '=');

        if (!(IS_CHAR(rbs, '0') || IS_CHAR(rbs, '1'))) throw new DecodeException(rbs, "Expected 0 or 1");
        var firstSymbol = rbs.advance();

        if (!IS_CHAR(rbs, '.')) return firstSymbol == '0' ? 0 : 1; rbs.advance();

        if (firstSymbol == '1')
            for (var i = 0; i < 3 && IS_CHAR(rbs, '0'); i++) bfr.push(rbs.advance());
        else
            for (var i = 0; i < 3 && IS_DIGIT(rbs); i++) bfr.push(rbs.advance());

        var value = (float) firstSymbol - '0';
        var exp = 10;

        for (var i = 0; i < 3 && (bfr.remains() > i); i++) {
            value += (float) (bfr.bytes[i] - '0') / exp;
            exp *= 10;
        }

        bfr.reset();
        return value;
    }

    public static void QUOTED_STRING(RequestByteStream rbs, Buffer bfr) {
        CHAR(rbs, '"');
        while (IS_QDTEXT(rbs) || IS_CHAR(rbs, '\\')) {
            if (IS_CHAR(rbs, '\\')) {
                bfr.push(rbs.advance());
                if (!IS_QUOTED_PAIR(rbs)) throw new DecodeException(rbs, "Expected quoted pair");
            }
            bfr.push(rbs.advance());
        }
        CHAR(rbs, '"');
    }

    public static void SKIP_OWS(RequestByteStream rbs) {
        byte b;
        while (true) {
            b = rbs.current();
            if (b == ' ' || b == '\t') rbs.advance();
            else break;
        }
    }

    public static boolean OWS_DELIMITER_OWS_SKIP(RequestByteStream rbs, char ch) {
        SKIP_OWS(rbs);
        var isDelimiter = IS_CHAR(rbs, ch);
        if (isDelimiter) { rbs.advance(); SKIP_OWS(rbs); }
        return isDelimiter;
    }

    public static void TOKENS_COMMA_SEPARATED(RequestByteStream rbs, Buffer bfr, ArrayList<String> dest) {
        if (!IS_TCHAR(rbs)) return;

        do {
            TOKEN_TCHAR(rbs, bfr);
            dest.add(bfr.toStringAndReset());
        } while (OWS_DELIMITER_OWS_SKIP(rbs, ','));
    }

    public static int PARAMETER(RequestByteStream rbs, Buffer bfr) {
        bfr.reset();
        var nameLength = 0;

        TOKEN_TCHAR(rbs, bfr);
        CHAR(rbs, '=');
        nameLength = bfr.remains();

        if (IS_CHAR(rbs, '"')) QUOTED_STRING(rbs, bfr);
        else TOKEN_TCHAR(rbs, bfr);

        return nameLength;
    }

    public static long UNSIGNED_LONG(RequestByteStream rbs) {
        var first = DIGIT(rbs);
        var value = 0;
        value += (first - '0');
        while (IS_DIGIT(rbs)) value = (value * 10) + (rbs.advance() - '0');
        return value;
    }

    public static void DAY_NAME(RequestByteStream rbs) {

        boolean found =
            switch ((rbs.lookahead(1) & ((byte) 7 << 2)) >>> 2) {
                case 0 -> {
                    for (var b : "Sat".getBytes(StandardCharsets.UTF_8)) {
                        if (rbs.advance() != b) {
                            yield false;
                        }
                    }
                    yield true;
                }
                case 1 -> {
                    for (var b : "Wed".getBytes(StandardCharsets.UTF_8)) {
                        if (rbs.advance() != b) {
                            yield false;
                        }
                    }
                    yield true;
                }
                case 2 -> {
                    for (var b : "Thu".getBytes(StandardCharsets.UTF_8)) {
                        if (rbs.advance() != b) {
                            yield false;
                        }
                    }
                    yield true;
                }
                case 3 -> {
                    for (var b : "Mon".getBytes(StandardCharsets.UTF_8)) {
                        if (rbs.advance() != b) {
                            yield false;
                        }
                    }
                    yield true;
                }
                case 4 -> {
                    for (var b: "Fri".getBytes(StandardCharsets.UTF_8)) {
                        if (rbs.advance() != b) {
                            yield false;
                        }
                    }
                    yield true;
                    }
                case 5 ->
                    switch ((rbs.lookahead(0) & ((byte) 1))) {
                        case 0 -> {
                            for (var b : "Tue".getBytes(StandardCharsets.UTF_8)) {
                                if (rbs.advance() != b) {
                                    yield false;
                                }
                            }
                            yield true;
                        }
                        case 1 -> {
                            for (var b : "Sun".getBytes(StandardCharsets.UTF_8)) {
                                if (rbs.advance() != b) {
                                    yield false;
                                }
                            }
                            yield true;
                        }
                        default -> false;
                    };
                default -> false;
            };

        if (!found) throw new DecodeException(rbs, "Expected day name");

    }
    
    private static int MONTH(RequestByteStream rbs) {

        int month =
            switch ((rbs.lookahead(0) & ((byte) 7))) {
                case 1 ->
                    switch ((rbs.lookahead(1) & ((byte) 1))) {
                        case 0 -> {
                            for (var b : "Apr".getBytes(StandardCharsets.UTF_8)) {
                                if (rbs.advance() != b) {
                                    yield -1;
                                }
                            }
                            yield 4;
                        }
                        case 1 -> {
                            for (var b : "Aug".getBytes(StandardCharsets.UTF_8)) {
                                if (rbs.advance() != b) {
                                    yield -1;
                                }
                            }
                            yield 8;
                        }
                        default -> -1;
                };
                case 2 ->
                    switch ((rbs.lookahead(1) & ((byte) 3 << 1)) >>> 1) {
                        case 0 -> {
                            for (var b : "Jan".getBytes(StandardCharsets.UTF_8)) {
                                if (rbs.advance() != b) {
                                    yield -1;
                                }
                            }
                            yield 1;
                        }
                        case 2 -> switch ((rbs.lookahead(2) & ((byte) 1 << 1)) >>> 1) {
                            case 0 -> {
                                for (var b : "Jul".getBytes(StandardCharsets.UTF_8)) {
                                    if (rbs.advance() != b) {
                                        yield -1;
                                    }
                                }
                                yield 7;
                            }
                            case 1 -> {
                                for (var b : "Jun".getBytes(StandardCharsets.UTF_8)) {
                                    if (rbs.advance() != b) {
                                        yield -1;
                                    }
                                }
                                yield 6;
                            }
                            default -> -1;
                        };
                        default -> -1;
                    };
                case 3 -> {
                    for (var b : "Sep".getBytes(StandardCharsets.UTF_8)) {
                        if (rbs.advance() != b) {
                            yield -1;
                        }
                    }
                    yield 9;
                }
                case 4 -> {
                    for (var b: "Dec".getBytes(StandardCharsets.UTF_8)) {
                        if (rbs.advance() != b) {
                            yield -1;
                        }
                    }
                    yield 12 ;
                    }
                case 5 ->
                    switch ((rbs.lookahead(2) & ((byte) 1))) {
                        case 0 -> {
                            for (var b : "Mar".getBytes(StandardCharsets.UTF_8)) {
                                if (rbs.advance() != b) {
                                    yield -1;
                                }
                            }
                            yield 3;
                        }
                        case 1 -> {
                            for (var b : "May".getBytes(StandardCharsets.UTF_8)) {
                                if (rbs.advance() != b) {
                                    yield -1;
                                }
                            }
                            yield 5;
                        }
                        default -> -1;
                    };
                case 6 ->
                    switch ((rbs.lookahead(0) & ((byte) 1 << 3)) >>> 3) {
                        case 0 -> {
                            for (var b : "Feb".getBytes(StandardCharsets.UTF_8)) {
                                if (rbs.advance() != b) {
                                    yield -1;
                                }
                            }
                            yield 2;
                        }
                        case 1 -> {
                            for (var b : "Nov".getBytes(StandardCharsets.UTF_8)) {
                                if (rbs.advance() != b) {
                                    yield -1;
                                }
                            }
                            yield 11;
                        }
                        default -> -1;
                    };
                case 7 -> {
                    for (var b: "Oct".getBytes(StandardCharsets.UTF_8)) {
                        if (rbs.advance() != b) {
                            yield -1;
                        }
                    }
                    yield 10 ;
                }
                default -> -1;
            };
        
        if (month == -1) throw new DecodeException(rbs, "Expected month name");
        
        return month;
    }

    public static void GMT(RequestByteStream rbs) {
        CHAR(rbs, 'G'); CHAR(rbs, 'M'); CHAR(rbs, 'T');
    }

    public static int NDIGIT(RequestByteStream rbs, int N) {
        int value = (DIGIT(rbs) - '0');
        for (var i = 0; i < N-1; i++) {
            value *= 10;
            value += (DIGIT(rbs) - '0');
        }

        return value;
    }

    public static LocalDateTime IMF_FIX_DATE(RequestByteStream rbs, Buffer bfr) {
        DAY_NAME(rbs); CHAR(rbs, ','); CHAR(rbs, ' ');
        var day = NDIGIT(rbs, 2); CHAR(rbs, ' ');
        var month = MONTH(rbs); CHAR(rbs, ' ');
        var year = NDIGIT(rbs, 4); CHAR(rbs, ' ');

        var hour = NDIGIT(rbs, 2); CHAR(rbs, ':');
        if (hour >= 24) throw new DecodeException(rbs, "Hour should be less then 24");

        var minute = NDIGIT(rbs, 2); CHAR(rbs, ':');
        if (minute >= 60) throw new DecodeException(rbs, "Minute should be less then 60");

        var second = NDIGIT(rbs, 2);
        if (second >= 60) throw new DecodeException(rbs, "Second should be less then 60");

        CHAR(rbs, ' '); GMT(rbs);

        return LocalDateTime.of(year, Month.of(month), day, hour, minute, second);
    }

    public static boolean IS_TOKEN68_CHAR(RequestByteStream rbs) {
        return IS_TOKEN68_TABLE[rbs.current()];
    }

    public static Authorization AUTHORIZATION(RequestByteStream rbs, Buffer bfr) {
        TOKEN_TCHAR(rbs, bfr);
        var authSchema = bfr.toStringAndReset();

        if (!IS_CHAR(rbs, ' '))
            return new Authorization(authSchema, null, new ArrayList<>());
        rbs.advance();

        if (!(IS_TCHAR(rbs) || IS_TOKEN68_CHAR(rbs))) throw new DecodeException(rbs, "Expected token68 or auth-params");

        var equalsCount = 0;
        var i = 0;
        while (IS_TCHAR_TABLE[rbs.lookahead(i)] || IS_TOKEN68_TABLE[rbs.lookahead(i)]) {
            var b = rbs.lookahead(i);
            if (equalsCount != 0 && b != '=') break;
            if (b == '=') equalsCount++;
            i++;
        }
        var nextChar = rbs.lookahead(i);
        var isAuthParams = IS_TCHAR_TABLE[nextChar] || nextChar == '"' || IS_TOKEN68_TABLE[nextChar];

        if (isAuthParams) {
            var authParams = new ArrayList<AuthParam>();
            AUTH_PARAMS(rbs, bfr, authParams);

            return new Authorization(authSchema, null, authParams);
        } else {
            while (i-- >= 0) bfr.push(rbs.advance());
            return new Authorization(authSchema, bfr.toStringAndReset(), new ArrayList<>());
        }
    }

    public static <T extends Authorization> void AUTHENTICATE(RequestByteStream rbs, Buffer bfr, ArrayList<T> dest) {
        if (!IS_TCHAR(rbs)) return;

        do {
            dest.add((T) AUTHORIZATION(rbs, bfr));
        } while (OWS_DELIMITER_OWS_SKIP(rbs, ','));
    }

    public static void AUTH_PARAMS(RequestByteStream rbs, Buffer bfr, ArrayList<AuthParam> dest) {
        if (!IS_TCHAR(rbs)) return;

        do {
            TOKEN_TCHAR(rbs, bfr);
            var tokenName = bfr.toStringAndReset();

            if (!OWS_DELIMITER_OWS_SKIP(rbs, '=')) throw new DecodeException(rbs, "Expected =");

            if (IS_CHAR(rbs, '"')) QUOTED_STRING(rbs, bfr);
            else TOKEN_TCHAR(rbs, bfr);

            dest.add(new AuthParam(tokenName, bfr.toStringAndReset()));
        } while (OWS_DELIMITER_OWS_SKIP(rbs, ','));
    }

    public static boolean IS_ENTITY_TAG(RequestByteStream rbs) {
        return IS_CHAR(rbs, 'W') || IS_CHAR(rbs, '"');
    }

    public static EntityTag ENTITY_TAG(RequestByteStream rbs, Buffer bfr) {
        var weak = false;
        if (IS_CHAR(rbs, 'W')) {rbs.advance(); CHAR(rbs, '/'); weak = true;}

        CHAR(rbs, '"');

        byte b = rbs.current();
        if ((b >= '#' && b <= '~') || b == '!') {
            do { b = rbs.advance(); bfr.push(b); } while ((b >= '#' && b <= '~') || b == '!');
        }

        CHAR(rbs, '"');
        var value = bfr.toStringAndReset();
        return weak ? new EntityTag.Weak(value) : new EntityTag.Default(value);
    }

    public static MatchEntitiesTags MATCH_ENTITIES_TAGS(RequestByteStream rbs, Buffer bfr) {
        if (IS_CHAR(rbs, '*')) return new MatchEntitiesTags.All();

        var value = new ArrayList<EntityTag>();

        if (!IS_ENTITY_TAG(rbs)) return new MatchEntitiesTags.EntitiesTags(value);
        value.add(ENTITY_TAG(rbs, bfr));

        while (OWS_DELIMITER_OWS_SKIP(rbs, ',')) {
            if (!IS_ENTITY_TAG(rbs)) throw new DecodeException(rbs, "Expected entity tag");
            value.add(ENTITY_TAG(rbs, bfr));
        }

        return new MatchEntitiesTags.EntitiesTags(value);
    }

    public static boolean IS_CTEXT_CHAR(RequestByteStream rbs) {
        var b = rbs.current();
        return IS_CTEXT_TABLE[b];
    }

    public static void PRODUCTS(RequestByteStream rbs, Buffer bfr, ArrayList<Product> dest) {
        do {
            TOKEN_TCHAR(rbs, bfr);
            var name = bfr.toStringAndReset();

            String version = null;
            if (IS_CHAR(rbs, '/')) {
                rbs.advance();
                TOKEN_TCHAR(rbs, bfr);
                version = bfr.toStringAndReset();
            }

            String comment = null;
            if (IS_CHAR(rbs, ' ')) {
                if (rbs.lookahead(1) == '(') {
                    rbs.advance(); rbs.advance();
                    while (IS_CTEXT_CHAR(rbs) || IS_CHAR(rbs, '\\')) {
                        if (IS_CTEXT_CHAR(rbs)) bfr.push(rbs.advance());
                        else {
                            rbs.advance();
                            if (!IS_QUOTED_PAIR(rbs)) throw new DecodeException(rbs, "Expected quoted pair");
                            bfr.push(rbs.advance());
                        }
                    }
                    comment = bfr.toStringAndReset();
                    CHAR(rbs, ')');
                }
            }

            dest.add(new Product(name, version, comment));
        } while (IS_CHAR(rbs, ' '));
    }

}
