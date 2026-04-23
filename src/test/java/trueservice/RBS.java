package trueservice;

import generate.routing.RouterGenerator;
import http.Buffer;
import http.RequestByteStream;
import http.dto.Headers;
import http.parsing.headers.Accept.AcceptParserEncoder;
import internalapi.CheetahApi;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.parsing.BaseDecoder.*;

public class RBS {

    public static class Cheetah implements CheetahApi {
        private final ArrayList<MemorySegment> memorySegments = new ArrayList<>();
        private int idx = 0;

        public Cheetah(String[] data) {
            for (var d: data) {
                memorySegments.add(MemorySegment.ofArray(d.getBytes(StandardCharsets.UTF_8)));
            }
        }

        @Override
        public @Nullable MemorySegment read() {
            if (idx == memorySegments.size()) return null;
//            ms.unload(); // ну типа забрал предыдущий ms
            return memorySegments.get(idx++);
        }

        @Override
        public MemorySegment getWriteBuffer() {
            return null;
        }

        @Override
        public void write(MemorySegment buffer) {

        }
    }

    @Test void woLookahead() {
        var dataToRead = new String[] {
            "text/html, application/xhtml+xml, application/xml;q=0.9, image/webp, */*;q=0.8",
            ", text/*;param=param\r\n"
        };

        var cheetah = new Cheetah(dataToRead);

        var rbs = new RequestByteStream(cheetah);

        var dest = new ArrayList<Headers.MediaRange>();
        var parser = new AcceptParserEncoder();

        parser.decode(rbs, new Buffer(1024), dest);
        IO.println(dest);

        var x = parser.create(dest);
        var y = 1;

    }

//    @Test void withLookahead() {
//
//        //        var x1 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l"), new Buffer());
////        var x2 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l==="), new Buffer());
////        var x3 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l="), new Buffer());
////        var x4 = parser.decode(createBS("Basic realm=\"Dev\", charset=\"UTF-8\""), new Buffer());
////        var x5 = parser.decode(createBS("Digest username=username, realm=\"realm\""), new Buffer());
//
//        var z = "Basic YWxhZGRpbjpvcGVuc2VzYW1l\r\n".toCharArray();
//        var dataToRead = new String[z.length];
//        for (var i = 0; i < dataToRead.length; i++) {
//            dataToRead[i] = String.valueOf(z[i]);
//        }
//
//        var cheetah = new Cheetah(dataToRead);
//
//        var rbs = new BaseDecoder.RequestByteStream(cheetah);
//
//        var parser = new AuthorizationParserEncoder();
//
//        var x = parser.decode(rbs, new BaseDecoder.Buffer());
//
//        var y = 1;
//
//    }


    @Test void f() {
        var routerCfgs = new ArrayList<RouterGenerator.RouteCfg>();

        routerCfgs.add(new RouterGenerator.RouteCfg("GET helloMudila", 1, "r1(bs);"));
        routerCfgs.add(new RouterGenerator.RouteCfg("GET helloHudioa", 2, "r2(bs);"));
        routerCfgs.add(new RouterGenerator.RouteCfg("GET helloHudila", 3, "r3(bs);"));

        var a = new RouterGenerator(routerCfgs);
        var b = a.getRouteSearchFunction();

        var x = 1;
    }

    void r1(RequestByteStream bs) {

        var z = 1;
    }

    void r2(RequestByteStream bs) {

        var z = 1;
    }

    void r3(RequestByteStream bs) {

        var z = 1;
    }

    @Test void route() {

        var z = "GET helloHudika ".toCharArray();
        var dataToRead = new String[z.length];
        for (var i = 0; i < dataToRead.length; i++) {
            dataToRead[i] = String.valueOf(z[i]);
        }

        var cheetah = new Cheetah(dataToRead);

        var bs = new RequestByteStream(cheetah);



        switch ((bs.lookahead(10) & ((byte) 3 << 0)) >>> 0) {
            case 0:
                switch ((bs.lookahead(14) & ((byte) 1 << 0)) >>> 0) {
                    case 0:
                        for (var b: "GET helloHudila".getBytes(StandardCharsets.UTF_8)) {
                            if (bs.advance() != b) {
                                // response 404
                            }
                        }
                        r3(bs);
                        break;
                    case 1:
                        for (var b: "GET helloHudioa".getBytes(StandardCharsets.UTF_8)) {
                            if (bs.advance() != b) {
                                var l = 1;
                                // response 404
                            }
                        }
                        r2(bs);
                        break;
                }
                break;
            case 1:
                for (var b: "GET helloMudila".getBytes(StandardCharsets.UTF_8)) {
                    if (bs.advance() != b) {
                        // response 404
                    }
                }
                r1(bs);
                break;
            case 2:
                // not found 404
                break;
            case 3:
                // not found 404
                break;
        }
    }

    @Test void lookahead() {
        var z = "GET helloHudika ".toCharArray();
        var dataToRead = new String[z.length];
        for (var i = 0; i < dataToRead.length; i++) {
            dataToRead[i] = String.valueOf(z[i]);
        }

        var cheetah = new Cheetah(dataToRead);

        var bs = new RequestByteStream(cheetah);

        var a = (char) bs.lookahead(0);
        var e = (char) bs.lookahead(1);
        var b = (char) bs.lookahead(5);
        var c = (char) bs.lookahead(9);
        var d = (char) bs.lookahead(10);

        var x = 1;
    }
}
