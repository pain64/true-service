package http;

import java.util.ArrayList;

import static http.JumpTables.*;
import static http.header.DTOs.*;

public class BaseParser {

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

    public interface ByteStream {
        void unadvance(byte b);
        byte advance(); // increment position and get current
    }


    public static byte CHAR(ByteStream bs, char ch) {
        byte b = bs.advance();
        if (b != ch) throw new RuntimeException("Expected " + ch);
        return (byte) ch;
    }

    public static boolean IS_CHAR(ByteStream bs, char ch) {
        byte b = bs.advance(); bs.unadvance(b);
        return b == ch;
    }

    public static byte DIGIT(ByteStream bs) {
        var b = bs.advance(); if (b >= '0' && b <= '9') return b;
        throw new RuntimeException("Expected DIGIT");
    }

    public static boolean IS_DIGIT(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return (b >= '0' && b <= '9');
    }

    public static byte ALPHA(ByteStream bs) {
        var b = bs.advance(); if (IS_ALPHA_TABLE[b]) return b;
        throw new RuntimeException("Expected ALPHA");
    }

    public static boolean IS_ALPHA(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return IS_ALPHA_TABLE[b];
    }

    public static byte ALPHA_DIGIT(ByteStream bs) {
        var b = bs.advance(); if (IS_ALPHA_OR_DIGIT_TABLE[b]) return b;
        throw new RuntimeException("Expected ALPHA/DIGIT");
    }

    public static byte HEXDIG(ByteStream bs) {
        byte b = bs.advance(); if (IS_HEXDIG_TABLE[b]) return b;
        throw new RuntimeException("Expected HEXDIG");
    }

    public static boolean IS_HEXDIG(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return IS_HEXDIG_TABLE[b];
    }

    public static boolean IS_TCHAR(ByteStream bs) {
        var b = bs.advance();
        bs.unadvance(b);
        return IS_TCHAR_TABLE[b];
    }

    public static boolean IS_QDTEXT(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return IS_QDTEXT_TABLE[b];
    }

    public static boolean IS_QUOTED_PAIR(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return IS_QUOTED_PAIR_TABLE[b];
    }

    private static void TOKEN(ByteStream bs, Buffer bfr, boolean[] IS_TOKEN_CHAR_TABLE) {
        var b = bs.advance();
        if (!IS_TOKEN_CHAR_TABLE[b]) throw new RuntimeException("Expected token");

        do {
            bfr.push(b);
            b = bs.advance();
        } while (IS_TOKEN_CHAR_TABLE[b]);
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

        if (!(IS_CHAR(bs, '0') || IS_CHAR(bs, '1'))) throw new RuntimeException("Expected 0 or 1");
        var firstSymbol = bs.advance();

        if (!IS_CHAR(bs, '.')) return firstSymbol == '0' ? 0 : 1;

        if (firstSymbol == '1')
            for (var i = 0; (i < 3) && IS_CHAR(bs, '0'); i++) bfr.push(bs.advance());
        else
            for (var i = 0; (i < 3) && IS_DIGIT(bs); i++) bfr.push(bs.advance());

        var value = (float) bfr.bytes[0] - '0';
        var exp = 10;
        for (var i = 0; i < 3 && (bfr.remains() > i); i++) {
            value += (float) (bfr.bytes[i] - '0') / exp;
            exp *= 10;
        }

        return value;
    }

    public static void QUOTED_STRING(ByteStream bs, Buffer bfr) {
        CHAR(bs, '"');
        while (IS_QDTEXT(bs) || IS_CHAR(bs, '\\')) {
            if (IS_CHAR(bs, '\\')) {
                bfr.push(bs.advance());
                if (!IS_QUOTED_PAIR(bs)) throw new RuntimeException("Expected quoted pair");
            }
            bfr.push(bs.advance());
        }
        CHAR(bs, '"');
    }

    public static void SKIP_OWS(ByteStream bs) {
        byte b; while (true) {b = bs.advance(); if (!(b == ' ' || b == '\t')) {bs.unadvance(b); break;}}
    }

    public static boolean OWS_DELIMITER_OWS_SKIP(ByteStream bs, char ch) {
        SKIP_OWS(bs);
        var v = IS_CHAR(bs, ch);
        if (v) bs.advance();
        SKIP_OWS(bs);
        return v;
    }

    public static ArrayList<String> TOKENS_COMMA_SEPARATED(ByteStream bs, Buffer bfr) {
        ArrayList<String> value = new ArrayList<>();
        if (!IS_TCHAR(bs)) return value;

        do {
            TOKEN_TCHAR(bs, bfr);
            value.add(bfr.toStringAndReset());
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));

        return value;
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

    public static String DAY_NAME(ByteStream bs, Buffer bfr) {
        bfr.reset();
        bfr.push(bs.advance()); bfr.push(bs.advance()); bfr.push(bs.advance());

        var value = bfr.toStringAndReset();

        try {
            DayName.valueOf(value);
        } catch (IllegalArgumentException _) {
            throw new RuntimeException("Expected day name");
        }

        return value;
    }

    enum MonthsName {Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec}

    public static String MONTH(ByteStream bs, Buffer bfr) {
        bfr.reset();
        bfr.push(bs.advance()); bfr.push(bs.advance()); bfr.push(bs.advance());

        var value = bfr.toStringAndReset();

        try {
            MonthsName.valueOf(value);
        } catch (IllegalArgumentException _) {
            throw new RuntimeException("Expected month name");
        }

        return value;
    }

    public static String GMT(ByteStream bs) {
        CHAR(bs, 'G'); CHAR(bs, 'M'); CHAR(bs, 'T'); return "GMT";
    }

    public static int NDIGIT(ByteStream bs, int N) {
        int value = 0;
        var exp = 1;

        for (var i = 0; i < N; i++) {
            value += (DIGIT(bs) - '0') * exp;
            exp *= 10;
        }

        return value;
    }

    public static String DATE1(ByteStream bs, Buffer bfr) {
        var day = NDIGIT(bs, 2); CHAR(bs, ' ');
        var month = MONTH(bs, bfr); CHAR(bs, ' ');
        var year = NDIGIT(bs, 4);

        return day + month + year;
    }

    public static String TIME_OF_DAY(ByteStream bs) {
        var hour = NDIGIT(bs, 2); CHAR(bs, ':');
        if (hour >= 24) throw new RuntimeException("Hour should be less then 24");

        var minute = NDIGIT(bs, 2); CHAR(bs, ':');
        if (minute >= 60) throw new RuntimeException("Minute should be less then 60");

        var second = NDIGIT(bs, 2);
        if (second >= 60) throw new RuntimeException("Second should be less then 60");

        return hour + ":" + minute + ":" + second;
    }

    public static String IMF_FIX_DATE(ByteStream bs, Buffer bfr) {
        var dayName = DAY_NAME(bs, bfr); CHAR(bs, ','); CHAR(bs, ' ');
        var date1 = DATE1(bs, bfr); CHAR(bs, ' ');
        var timeOfDay = TIME_OF_DAY(bs); CHAR(bs, ' ');

        return dayName + ", " + date1 + " " + timeOfDay + " " + GMT(bs);
    }

    public static boolean TOKEN68_CHECK(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return IS_TOKEN68_TABLE[b];
    }

    public static Authorization AUTHORIZATION(ByteStream bs, Buffer bfr) {
        TOKEN_TCHAR(bs, bfr);
        var authSchema = bfr.toStringAndReset();

        if (!IS_CHAR(bs, ' '))
            return new Authorization(authSchema, null, null);
        bs.advance();

        if (!(IS_TCHAR(bs) || TOKEN68_CHECK(bs))) throw new RuntimeException("Expected token68 or auth-params");

        var equalsCount = 0;
        while (IS_TCHAR(bs) || TOKEN68_CHECK(bs)) {
            var b = bs.advance();
            bfr.push(b);
            if (b == '=') equalsCount++;
        }
        for (var i = 0; i < bfr.remains(); i++) bs.unadvance(bfr.bytes[bfr.remains()- 1 -i]);
        bfr.reset();

        if (equalsCount == 1) {
            return new Authorization(authSchema, null, AUTH_PARAMS(bs, bfr));
        } else {
            while (TOKEN68_CHECK(bs)) bfr.push(bs.advance());
            return new Authorization(authSchema, bfr.toStringAndReset(), null);
        }
    }

    public static ArrayList<AuthParam> AUTH_PARAMS(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<AuthParam>();

        if (!IS_TCHAR(bs)) return value;

        do {
            TOKEN_TCHAR(bs, bfr);
            var tokenName = bfr.toStringAndReset();

            if (!OWS_DELIMITER_OWS_SKIP(bs, '=')) throw new RuntimeException("Expected =");

            if (IS_CHAR(bs, '"')) QUOTED_STRING(bs, bfr);
            else TOKEN_TCHAR(bs, bfr);

            value.add(new AuthParam.Token(tokenName, bfr.toStringAndReset()));
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));

        return value;
    }

    public static EntityTag ENTITY_TAG_OPT(ByteStream bs, Buffer bfr) {
        var weak = false;
        if (IS_CHAR(bs, 'W')) {CHAR(bs, '/'); weak = true;}

        if (!IS_CHAR(bs, '"')) {
            if (weak) throw new RuntimeException("Expected opaque-tag");
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
        var b = bs.advance(); bs.unadvance(b);
        return IS_CTEXT_TABLE[b];
    }

    public static ArrayList<Product> PRODUCTS(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<Product>();

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
                            if (!IS_QUOTED_PAIR(bs)) throw new RuntimeException("Expected quoted pair");
                            bfr.push(bs.advance());
                        }
                    }
                    comment = bfr.toStringAndReset();
                    CHAR(bs, ')');
                } else bs.unadvance((byte) ' ');
            }

            value.add(new Product(name, version, comment));
        } while (IS_CHAR(bs, ' '));

        return value;
    }

}
