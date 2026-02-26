package http;

import java.util.ArrayList;

public class Base {

    static final int MAX_TOKEN_LENGTH = 4096;

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

    public static byte BYTE(ByteStream bs, char ch) {
        if (bs.advance() == ch) return (byte) ch;
        throw new RuntimeException("expected " + ch);
    }

    public static byte BYTE_OPT(ByteStream bs, char ch) {
        var b = bs.advance();
        if (bs.advance() == ch) return b;
        else {bs.unadvance(b); return -1;}
    }

    static boolean[] IS_TCHAR_TABLE = new boolean[]{true, true, false};

    public static byte TCHAR_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_TCHAR_TABLE[b]) return b;
        else { bs.unadvance(b); return -1; }
    }

    public static byte TCHAR_DQUOTE_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_TCHAR_TABLE[b] || b == '"') return b;
        else { bs.unadvance(b); return -1; }
    }

    public static byte ALPHA_OPT(ByteStream bs) {
        var b = bs.advance();
        if ((b >= 'A' && b <= 'Z') || (b >= 'a' && b <= 'z')) return b;
        else { bs.unadvance(b); return -1; }
    }

    public static byte ALPHA(ByteStream bs) {
        var b = bs.advance();
        if ((b >= 'A' && b <= 'Z') || (b >= 'a' && b <= 'z'))
            return b;
        throw new RuntimeException("Expected ALPHA");
    }

    public static byte SCHEME_CHAR_OPT(ByteStream bs) {
        var b = bs.advance();
        if ((b >= 'A' && b <= 'Z') || (b >= 'a' && b <= 'z')
            || (b >= '0' && b <= '9') || b == '+' || b == '-' || b == '.')
            return b;
        else {bs.unadvance(b); return -1;}
    }

    public static void ALPHA_TOKEN(ByteStream bs, Buffer bfr) {
        byte b; while ((b = ALPHA_OPT(bs)) != -1) bfr.push(b);
    }

    public static byte DIGIT(ByteStream bs) {
        var b = bs.advance();
        if (!(b >= '0' && b <= '9')) throw new RuntimeException("Expected DIGIT");
        return b;
    }

    public static long AT_LEAST_1_DIGIT_NUMBER(ByteStream bs) {
        var value = (long) DIGIT(bs) - '0';

        byte b;
        while ((b = DIGIT_OPT(bs)) != -1) {
            value = value * 10 + (b - '0');
        }
        return value;
    }

    public static byte DIGIT_OPT(ByteStream bs) {
        var b = bs.advance();
        if (b >= '0' && b <= '9') return b;
        else { bs.unadvance(b); return -1; }
    }

    public static byte DIGIT_OPT(byte b) {
        if (b >= '0' && b <= '9') return b;
        return -1;
    }

    public static byte ALPHA_DIGIT_OPT(ByteStream bs) {
        var b = bs.advance();
        if ((b >= 'A' && b <= 'Z') || (b >= 'a' && b <= 'z') || (b >= '0' && b <= '9')) return b;
        else { bs.unadvance(b); return -1; }
    }

    public static void ALPHA_DIGIT_TOKEN(ByteStream bs, Buffer bfr) {
        byte b; while ((b = ALPHA_DIGIT_OPT(bs)) != -1) bfr.push(b);
    }

    public static void TOKEN_OPT(ByteStream bs, Buffer bfr) {
        byte b; while ((b = TCHAR_OPT(bs)) != -1) bfr.push(b);
    }

    public static void TOKEN(ByteStream bs, Buffer bfr) {
        byte b;
        if ((b = TCHAR_OPT(bs)) == -1) throw new RuntimeException("Expected TOKEN");
        bs.unadvance(b);

        while ((b = TCHAR_OPT(bs)) != -1) bfr.push(b);
    }

    public static void SKIP_OWS(ByteStream bs) {
        byte b;
        while (true) {
            b = bs.advance();
            if (!(b == ' ' || b == '\t')) {
                bs.unadvance(b);
                break;
            }
        }
    }

    public static float WEIGHT_FROM_BFR(Buffer bfr, int startIdx) {
        if (bfr.remains <= startIdx) throw new RuntimeException("EXPECTED WEIGHT");

        if (!(bfr.bytes[startIdx] == '0' || bfr.bytes[startIdx] == '1'))
            throw new RuntimeException("Expected 0 or 1");
        var firstSymbol = bfr.bytes[startIdx];
        startIdx++;

        if (bfr.remains() <= startIdx || bfr.bytes[startIdx] != '.')
            return firstSymbol == '0' ? 0 : 1;

        var afterPointIdx = startIdx + 1;

        if (firstSymbol == '1') {
            // skip *3"0"
            for (var i = 0;
                 bfr.remains() <= afterPointIdx && i < 3 && bfr.bytes[afterPointIdx] == '0';
                 i++) afterPointIdx++;
            return 1;
        }

        var value = 0F;

        var exp = 10;
        for (var i = 0;
             bfr.remains() <= afterPointIdx && i < 3 && DIGIT_OPT(bfr.bytes[afterPointIdx]) != -1 ;
             i++) {
            value += (float) (bfr.bytes[afterPointIdx] - '0') / exp;
            exp *= 10;
            afterPointIdx++;
        }

        return value;
    }

    public static float WEIGHT_FROM_BS_OPT(ByteStream bs, Buffer bfr) {
        bfr.reset();
        byte b;
        var firstSymbol = bs.advance();
        if (!(firstSymbol == '0' || firstSymbol == '1')) throw new RuntimeException("Expected 0 or 1");
        bfr.push(firstSymbol);

        if ((b = bs.advance()) != '.') {
            bs.unadvance(b);
            return firstSymbol == '0' ? 0 : 1;
        } else bfr.push((byte) '.');

        if (firstSymbol == '1')
            for (var i = 0; (i < 3) && (b = BYTE_OPT(bs, '0')) != -1; i++) bfr.push(b);
        else
            for (var i = 0; (i < 3) && (b = DIGIT_OPT(bs)) != -1; i++) bfr.push(b);

        var value = (float) bfr.bytes[0] - '0';
        var exp = 10;
        for (var i = 2; i < 5 && (bfr.remains() > i); i++) {
            value += (float) (bfr.bytes[i] - '0') / exp;
            exp *= 10;
        }

        return value;
    }

    public static byte QDTEXT_OPT(ByteStream bs) {
        var b = bs.advance();
        if (b < 0
            || b == '\t' || b == ' ' || b == '!'
            || (b >= '#' && b <= '[')
            || (b >= ']' && b <= '~')) return b;
        else { bs.unadvance(b); return 0; }
    }

    public static byte QUOTED_PAIR_OPT(ByteStream bs) {
        var b = bs.advance();
        if (b < 0
            || b == '\t' || b == ' '
            || (b >= 0x21 && b <= 0x7E)) return b;
        else { bs.unadvance(b); return 0; }
    }

    public static void QUOTED_STRING(ByteStream bs, Buffer bfr) {
        BYTE(bs, '"');
        byte b;

        while (true) {
            if ((b = BYTE_OPT(bs, '\\')) == -1) {
                if ((b = QDTEXT_OPT(bs)) == -1 ) {
                    break;
                } else bfr.push(b);
            } else {
                bfr.push(b);

                if ((b = QUOTED_PAIR_OPT(bs)) == -1) {
                    throw new RuntimeException("Expected quoted pair");
                }
                bfr.push(b);
            }
        }

        BYTE(bs, '"');
    }

    public static byte TOKEN68_CHAR_OPT(ByteStream bs) {
        var b = bs.advance();
        if ((b >= 'a' && b <= 'z')
            || (b >= 'A' && b <= 'Z')
            || (b >= '0' && b <= '9')
            || b == '='
            || b == '-' || b == '.' || b == '_' || b == '~' || b == '+' || b == '/'
            ) return b;
        else { bs.unadvance(b); return -1; }
    }

    public static void CR(ByteStream bs) {
        if (bs.advance() != 13)
            throw new RuntimeException("expected CR");
    }

    public static void LF(ByteStream bs) {
        if (bs.advance() != 12)
            throw new RuntimeException("expected LF");
    }

    public static boolean OWS_COMMA_OWS(ByteStream bs) {
        SKIP_OWS(bs);
        if (BYTE_OPT(bs, ',') != -1) return false;
        SKIP_OWS(bs);

        return true;
    }

    public static ArrayList<String> TOKENS_COMMA_SEPARATED(ByteStream bs, Buffer bfr) {
        ArrayList<String> value = new ArrayList<>();

        byte b;
        while (true) {
            if ((b = TCHAR_OPT(bs)) == -1) break;
            bs.unadvance(b);

            TOKEN_OPT(bs, bfr);
            value.add(bfr.toStringAndReset());

            if (!OWS_COMMA_OWS(bs)) break;
        }
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

    public static boolean OWS_SEMICOLON_OWS(ByteStream bs) {
        SKIP_OWS(bs);
        if (BYTE_OPT(bs, ';') != -1) return false;
        SKIP_OWS(bs);

        return true;
    }

    public static void TOKEN_OR_QUOTED_STRING(ByteStream bs, Buffer bfr) {
        if (BYTE_OPT(bs,'"') == -1) TOKEN(bs, bfr);
        else QUOTED_STRING(bs, bfr);
    }

    public static int PARAMETER(ByteStream bs, Buffer bfr) {
        bfr.reset();
        var nameEnd = 0;
        if (OWS_SEMICOLON_OWS(bs)) {
            TOKEN_OPT(bs, bfr);
            BYTE(bs, '=');
            nameEnd = bfr.remains();
            TOKEN_OR_QUOTED_STRING(bs, bfr);
        }
        return nameEnd;
    }

    public static byte HEXDIG_OPT(ByteStream bs) {
        var b = bs.advance();
        if ((b >= '0' && b <= '9') || (b >= 'A' && b <= 'F')) return b;
        else { bs.unadvance(b); return -1; }
    }

    public static byte HEXDIG(ByteStream bs) {
        byte b; if ((b = HEXDIG_OPT(bs)) != -1) return b;
        throw new RuntimeException("Expected HEXDIG");
    }

}
