package http;

import java.util.ArrayList;

import static http.JumpTables.*;
import static http.header.AuthenticationInfoHeader.AUTH_PARAMS;
import static http.header.AuthorizationHeader.*;

public class Base {

    static final int MAX_TOKEN_LENGTH = 4096;

    public static class Buffer {
        public final byte[] bytes = new byte[MAX_TOKEN_LENGTH];
        private int remains = 0;
        public void push(byte b) {
            bytes[remains++] = b;
        }
        public void push(char ch) {
            bytes[remains++] = (byte) ch;
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

    public sealed interface Method {
        record Get() implements Method { }
        record Head() implements Method { }
        record Post() implements Method { }
        record Put() implements Method { }
        record Delete() implements Method { }
        record Connect() implements Method { }
        record Options() implements Method { }
        record Trace() implements Method { }
        record Patch() implements Method { }
        record Token(String value) implements Method { }
    }

    public static byte CHAR(ByteStream bs, char ch) {
        byte b = bs.advance();
        if (b != ch) throw new RuntimeException("Expected " + ch);
        return b;
    }

    public static boolean CHAR_CHECK(ByteStream bs, char ch) {
        byte b = bs.advance(); bs.unadvance(b);
        return b == ch;
    }

    public static byte DIGIT(ByteStream bs) {
        var b = bs.advance(); if (b >= '0' && b <= '9') return b;
        throw new RuntimeException("Expected DIGIT");
    }

    public static boolean DIGIT_CHECK(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return (b >= '0' && b <= '9');
    }

    public static byte ALPHA(ByteStream bs) {
        var b = bs.advance(); if (IS_ALPHA_TABLE[b]) return b;
        throw new RuntimeException("Expected ALPHA");
    }

    public static boolean ALPHA_CHECK(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return IS_ALPHA_TABLE[b];
    }

    public static byte ALPHA_DIGIT(ByteStream bs) {
        var b = bs.advance(); if (IS_ALPHA_OR_DIGIT_TABLE[b]) return b;
        throw new RuntimeException("Expected ALPHA/DIGIT");
    }

    public static boolean ALPHA_DIGIT_OPT(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return IS_ALPHA_OR_DIGIT_TABLE[b];
    }

    public static byte HEXDIG(ByteStream bs) {
        byte b = bs.advance(); if (IS_HEXDIG_TABLE[b]) return b;
        throw new RuntimeException("Expected HEXDIG");
    }

    public static boolean HEXDIG_CHECK(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return IS_HEXDIG_TABLE[b];
    }

    public static byte TCHAR(ByteStream bs) {
        var b = bs.advance();
        if (IS_TCHAR_TABLE[b]) return b;
        throw new RuntimeException("Expected TCHAR");
    }

    public static boolean TCHAR_CHECK(ByteStream bs) {
        var b = bs.advance();
        bs.unadvance(b);
        return IS_TCHAR_TABLE[b];
    }

    public static byte SCHEME(ByteStream bs) {
        var b = bs.advance(); if (IS_SCHEME_TABLE[b]) return b;
        throw new RuntimeException("Expected SCHEME");
    }

    public static byte SCHEME_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_SCHEME_TABLE[b]) return b;
        else {bs.unadvance(b); return -1;}
    }

    public static boolean QDTEXT_CHECK(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return IS_QDTEXT_TABLE[b];
    }

    public static boolean QUOTED_PAIR_CHECK(ByteStream bs) {
        var b = bs.advance(); bs.unadvance(b);
        return IS_QUOTED_PAIR_TABLE[b];
    }

    public static void TOKEN(ByteStream bs, Buffer bfr, boolean[] TOKEN_TABLE, int max) {
        byte b;
        var i = 0;
        while(max == -1 || i < max ) {
            b = bs.advance();
            if (TOKEN_TABLE[b]) {bfr.push(b); i++;}
            else break;
        }
    }

    public static float WEIGHT_OPT(ByteStream bs, Buffer bfr) {
        bfr.reset();
        if(!OWS_SYMBOL_OWS_SKIP(bs, ';')) return -1;
        if(!CHAR_CHECK(bs, 'q')) {bs.unadvance((byte) ';'); return -1; }

        bs.advance(); CHAR(bs, '=');

        if (!(CHAR_CHECK(bs, '0') || CHAR_CHECK(bs, '1'))) throw new RuntimeException("Expected 0 or 1");
        var firstSymbol = bs.advance();

        if (!CHAR_CHECK(bs, '.')) return firstSymbol == '0' ? 0 : 1;

        if (firstSymbol == '1')
            for (var i = 0; (i < 3) && CHAR_CHECK(bs, '0'); i++) bfr.push(bs.advance());
        else
            for (var i = 0; (i < 3) && DIGIT_CHECK(bs); i++) bfr.push(bs.advance());

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
        while (QDTEXT_CHECK(bs) || CHAR_CHECK(bs, '\\')) {
            if (CHAR_CHECK(bs, '\\')) {
                bfr.push(bs.advance());
                if (!QUOTED_PAIR_CHECK(bs)) throw new RuntimeException("Expected quoted pair");
            }
            bfr.push(bs.advance());
        }
        CHAR(bs, '"');
    }

    public static void SKIP_OWS(ByteStream bs) {
        byte b; while (true) {b = bs.advance(); if (!(b == ' ' || b == '\t')) {bs.unadvance(b); break;}}
    }

    public static boolean OWS_SYMBOL_OWS_SKIP(ByteStream bs, char ch) {
        SKIP_OWS(bs);
        var v = CHAR_CHECK(bs, ch);
        if (v) bs.advance();
        SKIP_OWS(bs);
        return v;
    }

    public static ArrayList<String> TOKENS_COMMA_SEPARATED(ByteStream bs, Buffer bfr) {
        ArrayList<String> value = new ArrayList<>();
        if (!TCHAR_CHECK(bs)) return value;

        do {
            TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
            value.add(bfr.toStringAndReset());
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));

        return value;
    }

    public static class Parameter {
        public final String name;
        public final String value;

        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class Parameters {
        public final ArrayList<Parameter> value;

        public Parameters(ArrayList<Parameter> value) {
            this.value = value;
        }
    }

    public static int PARAMETER(ByteStream bs, Buffer bfr) {
        bfr.reset();
        var nameEnd = 0;
        if (OWS_SYMBOL_OWS_SKIP(bs, ';') && TCHAR_CHECK(bs)) {
            TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
            CHAR(bs, '=');
            nameEnd = bfr.remains();

            if (CHAR_CHECK(bs, '"')) QUOTED_STRING(bs, bfr);
            if (!TCHAR_CHECK(bs)) throw new RuntimeException("Expected token");

            TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
        }
        return nameEnd;
    }

    public static long ONE_OR_MORE_DIGIT_NUMBER(ByteStream bs) {
        var first = DIGIT(bs);
        var value = 0;
        value += (first - '0');
        while (DIGIT_CHECK(bs)) value = (value * 10) + (bs.advance() - '0');
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
        if(!TCHAR_CHECK(bs)) throw new RuntimeException("Expected auth-scheme");

        TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
        var authSchema = bfr.toStringAndReset();

        if (!CHAR_CHECK(bs, ' '))
            return new Authorization(authSchema, null, null);
        bs.advance();

        if (!(TCHAR_CHECK(bs) || TOKEN68_CHECK(bs))) throw new RuntimeException("Expected token68 or auth-params");

        var equalsCount = 0;
        while (TCHAR_CHECK(bs) || TOKEN68_CHECK(bs)) {
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

    public sealed interface AuthParam {
        record Token(String name, String value) implements AuthParam {}
    }

    public static ArrayList<AuthParam> AUTH_PARAMS(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<AuthParam>();

        while (TCHAR_CHECK(bs)) {
            TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
            var tokenName = bfr.toStringAndReset();

            if (!OWS_SYMBOL_OWS_SKIP(bs, '=')) throw new RuntimeException("Expected =");

            if (CHAR_CHECK(bs, '"')) QUOTED_STRING(bs, bfr);
            else if (TCHAR_CHECK(bs)) TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
            else throw new RuntimeException("Expected TCHAR or \"");

            value.add(new AuthParam.Token(tokenName, bfr.toStringAndReset()));

            OWS_SYMBOL_OWS_SKIP(bs, ',');
        }
        return value;
    }

    public sealed interface EntityTag {
        public record Default(String value) implements EntityTag {}
        public record Weak(String value) implements EntityTag {}
    }

    public sealed interface IfRangeType {
        public record EntityTag(Base.EntityTag value) implements IfRangeType, Base.EntityTag {}
        public record Date(String value) implements IfRangeType {}
    }

    public static EntityTag ENTITY_TAG_OPT(ByteStream bs, Buffer bfr) {
        var weak = false;
        if (CHAR_CHECK(bs, 'W')) {CHAR(bs, '/'); weak = true;}

        if (!CHAR_CHECK(bs, '"')) {
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

    public sealed interface MatchEntitiesTags {
        public record All() implements MatchEntitiesTags {}
        public record EntitiesTags(ArrayList<EntityTag> value) implements MatchEntitiesTags {}
    }

    public static MatchEntitiesTags MATCH_ENTITIES_TAGS(ByteStream bs, Buffer bfr) {
        if (CHAR_CHECK(bs, '*')) return new MatchEntitiesTags.All();

        var value = new ArrayList<EntityTag>();

        var entityTag = ENTITY_TAG_OPT(bs, bfr);
        if (entityTag != null) value.add(entityTag);

        while (OWS_SYMBOL_OWS_SKIP(bs, ',')) value.add(entityTag);

        return new MatchEntitiesTags.EntitiesTags(value);
    }

    public sealed interface RangeUnit {
        record Bytes() implements RangeUnit { }
        record Token(String value) implements RangeUnit { }
    }

}
