package http;

import java.util.ArrayList;

import static http.JumpTables.*;

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

    public static byte CHAR(ByteStream bs, char ch) {
        byte b = bs.advance();
        if (b != ch) throw new RuntimeException("Expected " + ch);
        return b;
    }

    public static byte CHAR_OPT(ByteStream bs, char ch) {
        byte b = bs.advance();
        if (b != ch) {bs.unadvance(b); return -1;}
        return b;
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

    public static void SKIP_OWS(ByteStream bs) {
        byte b; while (true) {b = bs.advance(); if (!(b == ' ' || b == '\t')) {bs.unadvance(b); break;}}
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
             bfr.remains() <= afterPointIdx && i < 3 && IS_DIGIT_TABLE[bfr.bytes[afterPointIdx]];
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
            for (var i = 0; (i < 3) && (b = CHAR_OPT(bs, '0')) != -1; i++) bfr.push(b);
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

    public static void QUOTED_STRING(ByteStream bs, Buffer bfr) {
        CHAR(bs, '"');
        byte b;

        while (true) {
            if ((b = CHAR_OPT(bs, '\\')) == -1) {
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

        CHAR(bs, '"');
    }

    public static boolean OWS_SYMBOL_OWS_SKIP(ByteStream bs, char ch) {
        SKIP_OWS(bs); if (CHAR_OPT(bs, ch) != -1) return false; SKIP_OWS(bs); return true;
    }

    public static ArrayList<String> TOKENS_COMMA_SEPARATED(ByteStream bs, Buffer bfr) {
        ArrayList<String> value = new ArrayList<>();

        byte b;
        while (true) {
            if ((b = TCHAR_OPT(bs)) == -1) break;
            bs.unadvance(b);

            TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
            if (bfr.remains() > 0) value.add(bfr.toStringAndReset());

            if (!OWS_SYMBOL_OWS_SKIP(bs, ',')) break;
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

    public static int PARAMETER(ByteStream bs, Buffer bfr) {
        bfr.reset();
        var nameEnd = 0;
        if (OWS_SYMBOL_OWS_SKIP(bs, ';')) {
            TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
            CHAR(bs, '=');
            nameEnd = bfr.remains();

            if (CHAR_OPT(bs, '"') == -1) {bs.unadvance((byte) '"'); QUOTED_STRING(bs, bfr);}
            else {
                var b = TCHAR(bs);
                bs.unadvance(b);
                TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
            }
        }
        return nameEnd;
    }

    public static long ONE_OR_MORE_DIGIT(ByteStream bs) {
        return 0;
    }

}
