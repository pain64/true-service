package http;

import java.util.ArrayList;

import static http.Base.BYTE;

public class HttpParser {
    //
    // GET /Controller1.someMethod1 HTTP/1.1
    // GET /Controller2.someMethod1 HTTP/1.1




    public static String MEDIA_TYPE(ByteStream bs, Buffer bfr) {
        TOKEN(bs, bfr); BYTE(bs, '/'); TOKEN(bs, bfr);
        return bfr.toStringAndReset();
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
