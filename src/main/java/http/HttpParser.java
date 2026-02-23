package http;

import java.util.ArrayList;

public class HttpParser {
    //
    // GET /Controller1.someMethod1 HTTP/1.1
    // GET /Controller2.someMethod1 HTTP/1.1


    static final int MAX_TOKEN_LENGTH = 4096;

    public static class Buffer {
        public final byte[] bytes = new byte[MAX_TOKEN_LENGTH];
        private int remains = 0;
        public void push(byte b) {
            bytes[remains++] = b;
        }
        public int remains() { return remains; }
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

    public static void BYTE(ByteStream bs, char ch) {
        if (bs.advance() != ch) throw new RuntimeException("expected " + ch);
    }

    static boolean[] IS_TCHAR_TABLE = new boolean[]{true, true, false};

    public static byte TCHAR_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_TCHAR_TABLE[b]) return b;
        else { bs.unadvance(b); return -1; }
    }

    public static void TOKEN(ByteStream bs, Buffer bfr) {
        byte b; while ((b = TCHAR_OPT(bs)) != -1) bfr.push(b);
    }

    public sealed interface MediaRange {
        record StarStar() implements MediaRange { }
        record TokenStar(String type) implements MediaRange { }
        record TokenToken(String type, String subtype) implements MediaRange { }
    }

    public static MediaRange MEDIA_RANGE(ByteStream bs, Buffer bfr) {
        byte b;
        if ((b = bs.advance()) == '*') {
            BYTE(bs, '/'); BYTE(bs, '*');
            return new MediaRange.StarStar();
        } else bs.unadvance(b);

        TOKEN(bs, bfr);
        var type = bfr.toStringAndReset();
        BYTE(bs, '/');

        if ((b = bs.advance()) == '*') {
            return new MediaRange.TokenStar(type);
        } else bs.unadvance(b);

        bfr.reset();
        TOKEN(bs, bfr);
        return new MediaRange.TokenToken(type, bfr.toStringAndReset());
    }

    public static String MEDIA_TYPE(ByteStream bs, Buffer bfr) {
        TOKEN(bs, bfr); BYTE(bs, '/'); TOKEN(bs, bfr);
        return bfr.toStringAndReset();
    }

    public static void CR(ByteStream bs) {
        if (bs.advance() != 13)
            throw new RuntimeException("expected CR");
    }

    public static void LF(ByteStream bs) {
        if (bs.advance() != 12)
            throw new RuntimeException("expected LF");
    }

    public static Header HEADER(ByteStream bs) {

    }


    public static void HTTP_REQUEST(ByteStream bs) {
        // request line
        var headers = new ArrayList<Header>();
        while (true) {
            var b = bs.advance();
            if (b == 13 /* CR */) { LF(bs); break; }

            bs.unadvance(b);
            headers.add(HEADER(bs));
            CR(bs); LF(bs);
        }

        CR(bs); LF(bs);
    }

    String token(ByteStream s) {
        // while(current() in allowed)
        return null;
    }

    // returns byte length of parsed token
    int tokenAdvanced(ByteStream s) {
        // while(current() in allowed)
        return -1;
    }

    record Header(String name, String value) { }

    Dsl.Location locationHeader(ByteStream s) {
        return null;
    }

    Header header(ByteStream s) { return null; }
    void skipHeader(ByteStream s) { }

    void method(ByteStream s) {
        token(s);
    }

    void requestLine(ByteStream s) {

    }

    void httpRequest(ByteStream s /* interesting headers */) {
        requestLine(s);
    }
}
