package http;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import static http.HttpParser.*;
import static http.JumpTables.*;
import static http.header.DTOs.*;

public class BaseDecoder {

    static final int MAX_TOKEN_LENGTH = 1024;

    public static class Buffer {
        public final byte[] bytes = new byte[MAX_TOKEN_LENGTH];
        private int remains = 0;
        public void push(byte b) {
            bytes[remains++] = b;
        }
        public int remains() { return remains; }
        public byte getLast(int i) { return bytes[remains()-i]; }
        public void reset() { remains = 0; }

        public String toStringAndReset() {
            var s = new String(bytes, 0, remains);
            reset();
            return s;
        }
    }

    public static class ByteStream {
        // todo: implement many chunks
        private byte[] value;
        private int position = 0;

        public ByteStream(byte[] value) {
            this.value = value;
        }

        public void unadvance(byte b) {
            position -= 1;
        } // return byte

        public byte current() {
            return value[position];
        } // get current

        public byte advance() {
            position += 1;
            return value[position-1];
        } // get current and increment position

        public long position() {
            return position;
        }

        public void reset() {
            position = 0;
        }
    }


    public static byte CHAR(ByteStream bs, char ch) {
        byte b = bs.current();
        if (b != ch) throw new HeaderDecodeException(bs.position(), "Expected " + ch);
        bs.advance();
        return (byte) ch;
    }

    public static boolean IS_CHAR(ByteStream bs, char ch) {
        return bs.current() == ch;
    }

    public static byte DIGIT(ByteStream bs) {
        var b = bs.current(); if (b >= '0' && b <= '9') { bs.advance(); return b; }
        throw new HeaderDecodeException(bs.position(), "Expected DIGIT");
    }

    public static boolean IS_DIGIT(ByteStream bs) {
        var b = bs.current();
        return (b >= '0' && b <= '9');
    }

    public static byte ALPHA(ByteStream bs) {
        var b = bs.current(); if (IS_ALPHA_TABLE[b]) {bs.advance(); return b;}
        throw new HeaderDecodeException(bs.position(), "Expected ALPHA");
    }

    public static boolean IS_ALPHA(ByteStream bs) {
        var b = bs.current();
        return IS_ALPHA_TABLE[b];
    }

    public static byte HEXDIG(ByteStream bs) {
        byte b = bs.current(); if (IS_HEXDIG_TABLE[b]) {bs.advance(); return b;}
        throw new HeaderDecodeException(bs.position(), "Expected HEXDIG");
    }

    public static boolean IS_HEXDIG(ByteStream bs) {
        var b = bs.current();
        return IS_HEXDIG_TABLE[b];
    }

    public static boolean IS_TCHAR(ByteStream bs) {
        var b = bs.current();
        return IS_TCHAR_TABLE[b];
    }

    public static boolean IS_QDTEXT(ByteStream bs) {
        var b = bs.current();
        return IS_QDTEXT_TABLE[b];
    }

    public static boolean IS_QUOTED_PAIR(ByteStream bs) {
        var b = bs.current();
        return IS_QUOTED_PAIR_TABLE[b];
    }

    private static void TOKEN(ByteStream bs, Buffer bfr, boolean[] IS_TOKEN_CHAR_TABLE) {
        var b = bs.current();
        if (!IS_TOKEN_CHAR_TABLE[b]) throw new HeaderDecodeException(bs.position(), "Expected token");

        do {
            b = bs.advance();
            bfr.push(b);
        } while (IS_TOKEN_CHAR_TABLE[bs.current()]);
    }

    public static void TOKEN_TCHAR(ByteStream bs, Buffer bfr) {
        TOKEN(bs, bfr, IS_TCHAR_TABLE);
    }

    public static void TOKEN_ALPHA(ByteStream bs, Buffer bfr) {
        TOKEN(bs, bfr, IS_ALPHA_TABLE);
    }

    public static void TOKEN_ALPHA_OR_DIGIT(ByteStream bs, Buffer bfr) {
        TOKEN(bs, bfr, IS_ALPHA_OR_DIGIT_TABLE);
    }

    public static float WEIGHT(ByteStream bs, Buffer bfr) {
        CHAR(bs, 'q'); CHAR(bs, '=');

        if (!(IS_CHAR(bs, '0') || IS_CHAR(bs, '1'))) throw new HeaderDecodeException(bs.position(), "Expected 0 or 1");
        var firstSymbol = bs.advance();

        if (!IS_CHAR(bs, '.')) return firstSymbol == '0' ? 0 : 1; bs.advance();

        if (firstSymbol == '1')
            for (var i = 0; (i < 3) && IS_CHAR(bs, '0'); i++) bfr.push(bs.advance());
        else
            for (var i = 0; (i < 3) && IS_DIGIT(bs); i++) bfr.push(bs.advance());

        var value = (float) firstSymbol - '0';
        var exp = 10;
        for (var i = 0; i < 3 && (bfr.remains() > i); i++) {
            value += (float) (bfr.bytes[i] - '0') / exp;
            exp *= 10;
        }

        bfr.reset();
        return value;
    }

    public static void QUOTED_STRING(ByteStream bs, Buffer bfr) {
        CHAR(bs, '"');
        while (IS_QDTEXT(bs) || IS_CHAR(bs, '\\')) {
            if (IS_CHAR(bs, '\\')) {
                bfr.push(bs.advance());
                if (!IS_QUOTED_PAIR(bs)) throw new HeaderDecodeException(bs.position(), "Expected quoted pair");
            }
            bfr.push(bs.advance());
        }
        CHAR(bs, '"');
    }

    public static void SKIP_OWS(ByteStream bs) {
        byte b;
        while (true) {
            b = bs.current();
            if (b == ' ' || b == '\t') bs.advance();
            else break;
        }
    }

    public static boolean OWS_DELIMITER_OWS_SKIP(ByteStream bs, char ch) {
        SKIP_OWS(bs);
        var isDelimiter = IS_CHAR(bs, ch);
        if (isDelimiter) { bs.advance(); SKIP_OWS(bs); }
        return isDelimiter;
    }

    public static void TOKENS_COMMA_SEPARATED(ByteStream bs, Buffer bfr, ArrayList<String> dest) {
        if (!IS_TCHAR(bs)) return;

        do {
            TOKEN_TCHAR(bs, bfr);
            dest.add(bfr.toStringAndReset());
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
    }

    public static int PARAMETER(ByteStream bs, Buffer bfr) {
        bfr.reset();
        var nameLength = 0;

        TOKEN_TCHAR(bs, bfr);
        CHAR(bs, '=');
        nameLength = bfr.remains();

        if (IS_CHAR(bs, '"')) QUOTED_STRING(bs, bfr);
        else TOKEN_TCHAR(bs, bfr);

        return nameLength;
    }

    public static long ONE_OR_MORE_DIGIT_NUMBER(ByteStream bs) {
        var first = DIGIT(bs);
        var value = 0;
        value += (first - '0');
        while (IS_DIGIT(bs)) value = (value * 10) + (bs.advance() - '0');
        return value;
    }

    enum DayName { Mon, Tue, Wed, Thu, Fri, Sat, Sun}

    public static void DAY_NAME(ByteStream bs, Buffer bfr) {
        bfr.reset();
        bfr.push(bs.advance()); bfr.push(bs.advance()); bfr.push(bs.advance());

        try {
            DayName.valueOf(bfr.toStringAndReset());
        } catch (IllegalArgumentException _) {
            throw new HeaderDecodeException(bs.position(), "Expected day name");
        }

    }

    private enum MonthsName {Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec}

    private static MonthsName MONTH(ByteStream bs, Buffer bfr) {
        bfr.reset();
        bfr.push(bs.advance()); bfr.push(bs.advance()); bfr.push(bs.advance());

        var value = bfr.toStringAndReset();

        try {
            return MonthsName.valueOf(value);
        } catch (IllegalArgumentException _) {
            throw new HeaderDecodeException(bs.position(), "Expected month name");
        }
    }

    public static void GMT(ByteStream bs) {
        CHAR(bs, 'G'); CHAR(bs, 'M'); CHAR(bs, 'T');
    }

    public static int NDIGIT(ByteStream bs, int N) {
        int value = 0;
        for (var i = 0; i < N; i++) value += (value * 10) + (DIGIT(bs) - '0');

        return value;
    }

    public static LocalDateTime IMF_FIX_DATE(ByteStream bs, Buffer bfr) {
        DAY_NAME(bs, bfr); CHAR(bs, ','); CHAR(bs, ' ');
        var day = NDIGIT(bs, 2); CHAR(bs, ' ');
        var month = MONTH(bs, bfr); CHAR(bs, ' ');
        var year = NDIGIT(bs, 4);

        var hour = NDIGIT(bs, 2); CHAR(bs, ':');
        if (hour >= 24) throw new HeaderDecodeException(bs.position(), "Hour should be less then 24");

        var minute = NDIGIT(bs, 2); CHAR(bs, ':');
        if (minute >= 60) throw new HeaderDecodeException(bs.position(), "Minute should be less then 60");

        var second = NDIGIT(bs, 2);
        if (second >= 60) throw new HeaderDecodeException(bs.position(), "Second should be less then 60");

        CHAR(bs, ' '); GMT(bs);

        return LocalDateTime.of(year, Month.of(month.ordinal()+1), day, hour, minute, second);
    }

    public static boolean TOKEN68_CHECK(ByteStream bs) {
        var b = bs.current();
        return IS_TOKEN68_TABLE[b];
    }

    public static Authorization AUTHORIZATION(ByteStream bs, Buffer bfr) {
        TOKEN_TCHAR(bs, bfr);
        var authSchema = bfr.toStringAndReset();

        if (!IS_CHAR(bs, ' '))
            return new Authorization(authSchema, null, new ArrayList<>());
        bs.advance();

        if (!(IS_TCHAR(bs) || TOKEN68_CHECK(bs))) throw new HeaderDecodeException(bs.position(), "Expected token68 or auth-params");

        var equalsCount = 0;
        while (IS_TCHAR(bs) || TOKEN68_CHECK(bs)) {
            var b = bs.advance();
            bfr.push(b);
            if (equalsCount != 0 && b != '=') break;
            if (b == '=') equalsCount++;
        }

        var isAuthParams = IS_TCHAR(bs) || IS_CHAR(bs, '"') || TOKEN68_CHECK(bs);

        for (var i = 0; i < bfr.remains(); i++) bs.unadvance(bfr.bytes[bfr.remains()- 1 -i]);
        bfr.reset();

        if (isAuthParams) {
            var authParams = new ArrayList<AuthParam>();
            AUTH_PARAMS(bs, bfr, authParams);

            return new Authorization(authSchema, null, authParams);
        } else {
            while (TOKEN68_CHECK(bs)) bfr.push(bs.advance());
            return new Authorization(authSchema, bfr.toStringAndReset(), new ArrayList<>());
        }
    }

    public static <T extends Authorization> void AUTHENTICATE(ByteStream bs, Buffer bfr, ArrayList<T> dest) {
        if (!IS_TCHAR(bs)) return;

        do {
            dest.add((T) AUTHORIZATION(bs, bfr));
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
    }

    public static void AUTH_PARAMS(ByteStream bs, Buffer bfr, ArrayList<AuthParam> dest) {
        if (!IS_TCHAR(bs)) return;

        do {
            TOKEN_TCHAR(bs, bfr);
            var tokenName = bfr.toStringAndReset();

            if (!OWS_DELIMITER_OWS_SKIP(bs, '=')) throw new HeaderDecodeException(bs.position(), "Expected =");

            if (IS_CHAR(bs, '"')) QUOTED_STRING(bs, bfr);
            else TOKEN_TCHAR(bs, bfr);

            dest.add(new AuthParam(tokenName, bfr.toStringAndReset()));
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
    }

    public static EntityTag ENTITY_TAG_OPT(ByteStream bs, Buffer bfr) {
        var weak = false;
        if (IS_CHAR(bs, 'W')) {CHAR(bs, '/'); weak = true;}

        if (!IS_CHAR(bs, '"')) {
            if (weak) throw new HeaderDecodeException(bs.position(), "Expected opaque-tag");
            else return null;
        } bs.advance();

        byte b = bs.advance();
        while ((b >= '#' && b <= '~') || b == '!') {bfr.push(b); b = bs.advance();}
        bs.unadvance(b);

        CHAR(bs, '"');
        var value = bfr.toStringAndReset();
        return weak ? new EntityTag.Weak(value) : new EntityTag.Default(value);
    }

    public static MatchEntitiesTags MATCH_ENTITIES_TAGS(ByteStream bs, Buffer bfr) {
        if (IS_CHAR(bs, '*')) return new MatchEntitiesTags.All();

        var value = new ArrayList<EntityTag>();

        var entityTag = ENTITY_TAG_OPT(bs, bfr);
        if (entityTag != null) value.add(entityTag);

        while (OWS_DELIMITER_OWS_SKIP(bs, ',')) value.add(entityTag);

        return new MatchEntitiesTags.EntitiesTags(value);
    }

    public static boolean CTEXT_CHECK(ByteStream bs) {
        var b = bs.current();
        return IS_CTEXT_TABLE[b];
    }

    public static void PRODUCTS(ByteStream bs, Buffer bfr, ArrayList<Product> dest) {
        do {
            //product
            TOKEN_TCHAR(bs, bfr);
            var name = bfr.toStringAndReset();

            String version = null;
            if (IS_CHAR(bs, '/')) {
                bs.advance();
                TOKEN_TCHAR(bs, bfr);
                version = bfr.toStringAndReset();
            }
            //comment

            String comment = null;
            if (IS_CHAR(bs, ' ')) {
                bs.advance();
                if (IS_CHAR(bs, '(')) {
                    bs.advance();
                    while (CTEXT_CHECK(bs) || IS_CHAR(bs, '\\')) {
                        if (CTEXT_CHECK(bs)) bfr.push(bs.advance());
                        else {
                            bs.advance();
                            if (!IS_QUOTED_PAIR(bs)) throw new HeaderDecodeException(bs.position(), "Expected quoted pair");
                            bfr.push(bs.advance());
                        }
                    }
                    comment = bfr.toStringAndReset();
                    CHAR(bs, ')');
                } else bs.unadvance((byte) ' ');
            }

            dest.add(new Product(name, version, comment));
        } while (IS_CHAR(bs, ' '));
    }

    public static Method METHOD(ByteStream bs, Buffer bfr) {
        TOKEN_TCHAR(bs, bfr);
        var token = bfr.toStringAndReset();

        return switch (token) {
            case "GET" -> new Method.Get();
            case "HEAD" -> new Method.Head();
            case "POST" -> new Method.Post();
            case "PUT" -> new Method.Put();
            case "DELETE" -> new Method.Delete();
            case "CONNECT" -> new Method.Connect();
            case "OPTIONS" -> new Method.Options();
            case "TRACE" -> new Method.Trace();
            case "PATCH" -> new Method.Patch();
            default -> new Method.Token(token);
        };
    }

    public static void METHODS(ByteStream bs, Buffer bfr, ArrayList<Method> dest) {
        if (!IS_TCHAR(bs)) return;

        do {
            dest.add(METHOD(bs, bfr));
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
    }



}
