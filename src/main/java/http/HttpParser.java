package http;

public class HttpParser {
    //
    // GET /Controller1.someMethod1 HTTP/1.1
    // GET /Controller2.someMethod1 HTTP/1.1


    static final int MAX_TOKEN_LENGTH = 4096;
    private final byte[] buffer = new byte[MAX_TOKEN_LENGTH];

    interface ByteStream {
        byte current();
        byte advance(); // increment position and get current
    }

    String token(ByteStream s) {
        // while(current() in allowed)
        return null;url = git@github.com:pain64/true-service.git
    }

    // returns byte length of parsed token
    int tokenAdvanced(ByteStream s) {
        // while(current() in allowed)
        return -1;
    }

    record Header(String name, String value) {}

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
