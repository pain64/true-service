import java.lang.foreign.MemorySegment;
import java.net.URI;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.nio.charset.StandardCharsets.*;

public class TestHttpParse {

    static class Lex {
        int i = 0;
        byte ch() {
            i++;
            return 0;
        }

        void advance(int offset) {}

        byte lookahead(int offfset) {
            i++;
            return 0;
        }

        boolean avail(int offset) { return false; }

        boolean matches(String str) {
            return false;
        }
    }

    static int parseSlowPath(Lex l) {
        return 0;
    }

    static int parseForWebApiEntry(Lex l) {



//        if (l.availAndEqual(6, ''))
//            switch (l.ch()) {
//                case 'c' -> {
//                    if (!l.matches("on")) return;
//                    switch (l.ch()) {
//                        case 'n' -> {
//                            if (!l.matches("ection")) return;
//                            // on connection header
//                        }
//                        case 't' -> {
//                            if (!l.matches("ent-type")) return;
//                            // on content-type header
//                        }
//                    }
//                }
//                case 't' -> {
//                    if (!l.matches("transfer-encoding")) return;
//                    // on transfer-encoding header
//                }
//            }
        return 0;
    }

    Void handleMalformed(Lex l) {return null; }

    static class Connection {
        static Connection parse(Lex l) { return null; }
    }
    static class TransferEncoding {
        static TransferEncoding parse(Lex l) { return null; }
    }

    Void handleEndpoint(Lex l) {
        // handle path variables
        // handle query string

        var connection = (Connection) null; // Connection header
        var transferEncoding = (TransferEncoding) null;

        while (true) {
            // switch (handleEndOfHeaders(l)) {
            //     case End -> break;
            //     case Malformed -> return;
            // }
            // vs

            if (l.ch() == '\r') {
                l.advance(1);
                if (l.ch() == '\n') {
                    l.advance(1);
                    break; // end of headers
                }
                else return handleMalformed(l);
            }

            boolean isHeaderParsed = false;

            if (!l.avail(6))
                switch (l.lookahead(6)) {
                    case 'c' -> {
                        // l.availAndMatches("Connection:")
                        if (l.avail(12) && l.matches("Connection:")) {
                            connection = Connection.parse(l);
                            isHeaderParsed = true;
                        }
                    }
                    case 'f' -> {
                        if (l.avail(19) && l.matches("Transfer-Encoding:")) {
                            transferEncoding = TransferEncoding.parse(l);
                            isHeaderParsed = true;
                        }
                    }
                    case 'n' -> {
                        if (l.avail(16) && l.matches("Content-Type:")) {
                            isHeaderParsed = true;
                        }
                    }
                    default -> {
                        // skipHeader()
                        isHeaderParsed = true;
                        // todo: skip
                    }
                }

            if (!isHeaderParsed) {
                // wtf ???
            }
        }

        if (connection == null) {} // keep alive
        if (transferEncoding == null) {} // hahaha
        // assume transfer-encoding == chunked
        // iterate over each chunk
        // var obj = new JsonParse(new Lex(l, CHUNK_SIZE))
        // var resp = httpApiObject.endpoint(obj.f1, obj.f2, coockie);
        //

        return null;



    }

    void handleRequest(Lex l) {

    }


    static class Module1 {

    }

    static boolean eq(MemorySegment m, int i, String content) {
        var bytes = content.getBytes(UTF_8);
        return MemorySegment.mismatch(
            m, i, i + bytes.length, MemorySegment.ofArray(bytes), 0, bytes.length
        ) == -1;
    }

    static void main() {
        // 'C' -> 'on' -> 'n' => 'ection' =>
        //             -> 't' => 'ent-Type' =>
        // 'T' -> 'ransfer-Encoding' => ''
        // vs
        // c ->
        // n ->
        // f ->

        var m = MemorySegment.ofArray(
            """
                GET /hello HTTP 1.1\r
                Connection: keep-alive\r
                Transfer-Encoding: chunked\r
                Content-Type: application/json\r
                \r
                """.getBytes(UTF_8)
        );

        var i = 0;
        var a = 34;
        var f = m;

        if (a < 1)
            f = m;
        switch (m.get(JAVA_BYTE, i + 0)) {
            case 'G' -> {
                if (eq(m, i + 6, "GET /hello HTTP 1.1\r\n")) {}
            }
            case 'P' -> { }
            default -> { }
        }

        // fast path


        // GET /hello
        // POST /world

        // Гипотезы:
        //     заголовки лежат в буфере полностью (в общем случае)

        // GET /hello HTTP 1.1
        // Connection: keep-alive\r\n
        // Transfer-Encoding: chunked\r\n
        // Content-Type: application/json\r\n
        // \r\n

        // 1. Мы знаем сколько байт минимум должно быть в request line
        //    - если мы прочитали меньше чем нужно, то дочитываем буфер?
        //    - request line должен влезть в 4К ???
        //

    }
}
