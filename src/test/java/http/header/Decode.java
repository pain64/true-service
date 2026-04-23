package http.header;


import generate.endpoint.RequestParserGenerator;
import generate.routing.RouterGenerator;
import generate.searchtree.SearchTreeAlg;
import http.parsing.headers.Accept.AcceptParserEncoder;
import http.parsing.headers.Auth.*;
import http.parsing.headers.Content.ContentTypeParserEncoder;
import http.parsing.headers.DateParserEncoder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static generate.endpoint.RequestParserGenerator.*;
import static generate.routing.RouterGenerator.*;
import static http.dto.Headers.*;

public class Decode {
//    public static RequestByteStream createBS(String value) {
//        var cheetah = new RBS.Cheetah(new String[] {value});
//        return new RequestByteStream((value + "\n").getBytes(StandardCharsets.UTF_8));
//    }
//
//    @Test void acceptEncoding() {
//        var dest = new ArrayList<EncodingWithWeight>();
//        var parser = new AcceptEncodingParserEncoder();
//        parser.decode(createBS("identity, deflate, gzip;q=1.0, *;q=0.5"), new Buffer(), dest);
//        parser.decode(createBS(""), new Buffer(), dest);
//        IO.println(dest);
//        var x = parser.create(dest);
//        var y = 1;
//    }
//
//    @Test void acceptLanguage() {
//        var dest = new ArrayList<LanguageRangeWithWeight>();
//        var parser = new AcceptLanguageParserEncoder();
//        parser.decode(createBS("fr-CH, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5"), new Buffer(), dest);
//        parser.decode(createBS(""), new Buffer(), dest);
//        IO.println(dest);
//        var x = parser.create(dest);
//        var y = 1;
//    }
//
//    @Test void accept() {
//        var dest = new ArrayList<MediaRange>();
//        var parser = new AcceptParserEncoder();
//        parser.decode(createBS("text/html, application/xhtml+xml, application/xml;q=0.9, image/webp, */*;q=0.8"), new Buffer(), dest);
//        parser.decode(createBS("text/*;param=param"), new Buffer(), dest);
//        parser.decode(createBS("text/html;param=\"pa\\ ram\""), new Buffer(), dest);
//        parser.decode(createBS(""), new Buffer(), dest);
//        IO.println(dest);
//        var x = parser.create(dest);
//        var y = 1;
//    }
//
//    @Test void acceptRanges() {
//        var parser = new AcceptRangesParserEncoder();
//        var x = parser.decode(createBS("bytes"), new Buffer());
//        var y = 1;
//    }
//
//    @Test void authenticationInfo() {
//        var dest = new ArrayList<AuthParam>();
//        var parser = new AuthenticationInfoParserEncoder();
//        parser.decode(createBS("nextnonce=\"4ee60b\", rspauth=\"3e8f9d1c2a5b4d7e\", qop=auth, cnonce=\"0a4f113b\", nc=00000001"), new Buffer(), dest);
//
//        var x = parser.create(dest);
//        var y = 1;
//    }
//
//    @Test void authorization() {
//        var parser = new AuthorizationParserEncoder();
//        var x1 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l"), new Buffer());
//        var x2 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l==="), new Buffer());
//        var x3 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l="), new Buffer());
//        var x4 = parser.decode(createBS("Basic realm=\"Dev\", charset=\"UTF-8\""), new Buffer());
//        var x5 = parser.decode(createBS("Digest username=username, realm=\"realm\""), new Buffer());
//
//        var y = 1;
//    }
//
//    @Test void proxyAuthenticate() {
//        var dest = new ArrayList<Challenge>();
//        var parser = new ProxyAuthenticateParserEncoder();
//        parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l, Basic YWxhZGRpbjpvcGVuc2VzYW1l"), new Buffer(), dest);
//        parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l, Basic realm=\"Dev\", charset=\"UTF-8\""), new Buffer(), dest);
//
//        var x = parser.create(dest);
//        var y = 1;
//    }
//
//
//    @Test void proxyAuthenticationInfo() {
//        var dest = new ArrayList<AuthParam>();
//        var parser = new ProxyAuthenticationInfoParserEncoder();
//        parser.decode(createBS("nextnonce=\"4ee60b\", rspauth=\"3e8f9d1c2a5b4d7e\", qop=auth, cnonce=\"0a4f113b\", nc=00000001"), new Buffer(), dest);
//
//        var x = parser.create(dest);
//        var y = 1;
//    }
//
//    @Test void proxyAuthorization() {
//        var parser = new ProxyAuthorizationParserEncoder();
//        var x1 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l"), new Buffer());
//        var x2 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l==="), new Buffer());
//        var x3 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l="), new Buffer());
//        var x4 = parser.decode(createBS("Basic realm=\"Dev\", charset=\"UTF-8\""), new Buffer());
//        var x5 = parser.decode(createBS("Digest username=username, realm=\"realm\""), new Buffer());
//
//        var y = 1;
//
//    }
//
//    @Test void WWWAuthenticate() {
//        var dest = new ArrayList<Challenge>();
//        var parser = new WWWAuthenticateParserEncoder();
//        parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l, Basic YWxhZGRpbjpvcGVuc2VzYW1l"), new Buffer(), dest);
//        parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l, Basic realm=\"Dev\", charset=\"UTF-8\""), new Buffer(), dest);
//
//        var x = parser.create(dest);
//        var y = 1;
//    }
//
//    @Test void ifMatch() {
//        var parser = new IfMatchParserEncoder();
//        var x1 = parser.decode(createBS("*"), new Buffer());
//        var x2 = parser.decode(createBS(""), new Buffer());
//        var x3 = parser.decode(createBS("\"67ab43\", W/\"54ed21\", \"7892dd\""), new Buffer());
//    }
//
//    @Test void ifModifiedSince() {
//        var parser = new IfModifiedSinceParserEncoder();
//        var x1 = parser.decode(createBS("Wed, 21 Oct 2015 07:28:00 GMT"), new Buffer());
//        var y = 1;
//    }
//
//    @Test void ifNoneMatch() {
//        var parser = new IfMatchParserEncoder();
//        var x1 = parser.decode(createBS("*"), new Buffer());
//        var x2 = parser.decode(createBS(""), new Buffer());
//        var x3 = parser.decode(createBS("\"67ab43\", W/\"54ed21\", \"7892dd\""), new Buffer());
//    }
//
//    @Test void ifRange() {
//        var parser = new IfRangeParserEncoder();
//        var x1 = parser.decode(createBS("Wed, 21 Oct 2015 07:28:00 GMT"), new Buffer());
//        var x2 = parser.decode(createBS("Sun, 21 Oct 2015 07:28:00 GMT"), new Buffer());
//        var x3 = parser.decode(createBS("W/\"54ed21\""), new Buffer());
//        var x4 = parser.decode(createBS("\"54ed21\""), new Buffer());
//
//        var y = 1;
//    }
//
//    @Test void ifUnmodifiedSince() {
//        var parser = new IfUnmodifiedSinceParserEncoder();
//        var x1 = parser.decode(createBS("Wed, 21 Oct 2015 07:28:00 GMT"), new Buffer());
//        var y = 1;
//    }
//
//    @Test void contentEncoding() {
//
//    }
//
//    @Test void contentLength() {
//        var parser = new ContentLengthParserEncoder();
//        var x1 = parser.decode(createBS("123"), new Buffer());
//        var y = 1;
//    }
//
//    @Test void contentLocation() {
//        //TODO
//    }
//
//    @Test void contentRange() {
//        var parser = new ContentRangeParserEncoder();
//        var x1 = parser.decode(createBS("bytes */12424"), new Buffer());
//        var x2 = parser.decode(createBS("bytes 0-1233/12424"), new Buffer());
//        var x3 = parser.decode(createBS("bytes 341-343/*"), new Buffer());
//
//        var y = 1;
//    }
//
//    @Test void contentType() {
//        var parser = new ContentTypeParserEncoder();
//        var x1 = parser.decode(createBS("text/html"), new Buffer());
//        var x2 = parser.decode(createBS("multipart/form-data; boundary=ExampleBoundaryString"), new Buffer());
//
//        var y = 1;
//    }
//
//    //todo COOKIE
//
//    @Test void accessControlAllowCredentials() {
//        var parser = new AccessControlAllowCredentialsParser();
//        var x1 = parser.decode(createBS("true"), new Buffer());
//
//        var y = 1;
//    }
//
//    @Test void accessControlAllowHeaders() {
//        var parser = new AccessControlAllowHeadersParser();
//        var dest = new ArrayList<String>();
//
//        parser.decode(createBS("Accept, Content-Length"), new Buffer(), dest);
//        parser.decode(createBS("Content-Type"), new Buffer(), dest);
//
//        var y = 1;
//    }
//
//    @Test void accessControlAllowMethods() {
//        var parser = new AccessControlAllowMethodsParser();
//        var dest = new ArrayList<Method>();
//
//        parser.decode(createBS("GET, POST"), new Buffer(), dest);
//        parser.decode(createBS("PUT"), new Buffer(), dest);
//
//        var y = 1;
//    }
//
//    @Test void accessControlAllowOrigin() {
//        //TODO
//    }
//
//    @Test void accessControlExposeHeaders() {
//        var parser = new AccessControlExposeHeadersParser();
//        var dest = new ArrayList<String>();
//
//        parser.decode(createBS("Content-Encoding, Kuma-Revision"), new Buffer(), dest);
//        parser.decode(createBS("*"), new Buffer(), dest);
//
//        var y = 1;
//    }
//
//    @Test void accessControlMaxAge() {
////        var parser = new AccessControlMaxAgeParser();
////        var dest = new ArrayList<String>();
////
////        parser.decode(createBS("Content-Encoding, Kuma-Revision"), new Buffer(), dest);
////        parser.decode(createBS("*"), new Buffer(), dest);
////
////        var y = 1;
//    }
//
//    @Test void accessControlRequestHeaders() {
//
//    }
//
//    @Test void accessControlRequestMethod() {
//
//    }
//
//    @Test void TE() {
//
//    }
//
//    @Test void trailer() {
//
//    }
//
//    @Test void allow() {
//
//    }
//
//    @Test void connection() {
//        var bs2 = createBS("keep-alive, hello");
//        var dest = new ArrayList<String>();
//        var parser = new ConnectionParserEncoder();
//        parser.decode(bs2, new Buffer(), dest);
//        IO.println(dest);
//        var x = parser.create(dest);
//        var y = 1;
//    }
//
//    @Test void date() {
//
//    }
//
//
//    @Test void ETag() {
//
//    }
//
//    @Test void expect() {
//
//    }
//
//    @Test void lastModified() {
//
//    }
//
//    @Test void maxForwards() {
//        var bs = createBS("12323423");
//        var parser = new MaxForwardsParserEncoder();
//        var x = parser.decode(bs, new Buffer());
//
//        var y = 1;
//    }
//
//    @Test void range() {
//
//    }
//
//    @Test void retryAfter() {
//
//    }
//
//    @Test void server() {
//
//    }
//
//    @Test void upgrade() {
//
//    }
//
//    @Test void userAgent() {
//
//    }
//
//    @Test void vary() {
//
//    }
//
//    @Test void via() {
//
//    }
//
//
    @Test void x() {
        ArrayList<Class<?>> parsers = new ArrayList<>(
            List.of(ContentTypeParserEncoder.class, AuthorizationParserEncoder.class, DateParserEncoder.class,
                AcceptParserEncoder.class, AuthenticationInfoParserEncoder.class));

        ArrayList<PathParameter> pathParameters = new ArrayList<>();
        ArrayList<QueryParameter> queryParameters = new ArrayList<>();

        pathParameters.add(new PathParameter(String.class, "pathName",
            (paramName) -> "TOKEN_PERCENT_ENCODED(rbs, bfr);\npathName = bfr.toStringAndReset();"));
        pathParameters.add(new PathParameter(Integer.class, "pathId",
            (s) -> s + " = (int) UNSIGNED_LONG(rbs);"));

        queryParameters.add(new QueryParameter(1, String.class, true, "name", "queryName",
            (paramName) -> "TOKEN_PERCENT_ENCODED(rbs, bfr);\nqueryName = bfr.toStringAndReset();"));
        queryParameters.add(new QueryParameter(2, Integer.class, false, "id", "queryId",
            (s) -> s + " = (int) UNSIGNED_LONG(rbs);"));

        ArrayList<HeaderConfiguration> valueHeadersCfg = new ArrayList<>();
        ArrayList<ValueListHeaderConfiguration> valueListHeadersCfg = new ArrayList<>();
        ArrayList<CookieConfiguration> cookieHeadersCfg = new ArrayList<>();

        valueHeadersCfg.add(new HeaderConfiguration(1, ContentType.class, "contentType", true, "Content-Type", (v) -> "contentTypeParserEncoder.decode(rbs, bfr);"));
        valueHeadersCfg.add(new HeaderConfiguration(2, Authorization.class, "auth", false, "Authorization", (v) -> "authorizationParserEncoder.decode(rbs, bfr);"));
        valueHeadersCfg.add(new HeaderConfiguration(3, Date.class, "date", true, "Date", (v) -> "dateParserEncoder.decode(rbs, bfr);"));

        valueListHeadersCfg.add(new ValueListHeaderConfiguration(4, Accept.class, "accept", "Accept", MediaRange.class, (valueParamName) -> "acceptParserEncoder.decode(rbs, bfr, "+ valueParamName + ");"));
        valueListHeadersCfg.add(new ValueListHeaderConfiguration(5, AuthenticationInfo.class, "authInfo", "Authentication-Info", AuthParam.class, (valueParamName) -> "authenticationInfoParserEncoder.decode(rbs, bfr, " + valueParamName + ");"));

        cookieHeadersCfg.add(new CookieConfiguration(1, String.class, "JSessionId", true, "JSESSIONID",
            (pName) -> "TOKEN_COOKIE(rbs, bfr);" + "\n" + pName + " = bfr.toStringAndReset();"));
        cookieHeadersCfg.add(new CookieConfiguration(2, Integer.class, "secret", false,"SECRET",
            (pName) -> pName + " = (int) UNSIGNED_LONG(rbs);"));

        var endpointGenerator = new RequestParserGenerator("HelloEndpoint", "GET", "hello\\\\", parsers, pathParameters, queryParameters, valueHeadersCfg, valueListHeadersCfg, cookieHeadersCfg);

        var c = endpointGenerator.generateEndpointClass();

        var x = 1;

    }
//
//    void hello(RequestByteStream bs) {}
//
//    void privet(RequestByteStream bs) {}
//
//    void pupupu(RequestByteStream bs) {}
//
    @Test void f() {
        var routerCfgs = new ArrayList<RouteCfg>();

        routerCfgs.add(new RouteCfg("GET hello", 1, "hello"));
        routerCfgs.add(new RouteCfg("GET privet", 2, "privet"));
        routerCfgs.add(new RouteCfg("POST pupupu", 3, "pupupu"));

        var a = new RouterGenerator(routerCfgs);
        var c = a.getRouterClass();

        var x = 1;

        //
    }

//    enum DayName { Mon, Tue, Wed, Thu, Fri, Sat, Sun}

    public class S implements SearchTreeAlg.SearchString {
        private final String s;
        private final int id;

        public S(String s, int id) {
            this.s = s;
            this.id = id;
        }


        @Override
        public String getString() {
            return s;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    @Test void dayNameTree() {
        var ss = new ArrayList<SearchTreeAlg.SearchString>();

        ss.add(new S("Mon", 0));
        ss.add(new S("Tue", 0));
        ss.add(new S("Wed", 0));
        ss.add(new S("Thu", 0));
        ss.add(new S("Fri", 0));
        ss.add(new S("Sat", 0));
        ss.add(new S("Sun", 0));

        var tree = SearchTreeAlg.createSearchTree(ss);

        var code = SearchTreeAlg.generateSearchTreeCode(tree, 0);


        var x = 1;


        //
    }

    //    private enum MonthsName {Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec}
    @Test void monthNameTree() {
        var ss = new ArrayList<SearchTreeAlg.SearchString>();

        ss.add(new S("Jan", 1));
        ss.add(new S("Feb", 2));
        ss.add(new S("Mar", 3));
        ss.add(new S("Apr", 4));
        ss.add(new S("May", 5));
        ss.add(new S("Jun", 6));
        ss.add(new S("Jul", 7));
        ss.add(new S("Aug", 8));
        ss.add(new S("Sep", 9));
        ss.add(new S("Oct", 10));
        ss.add(new S("Nov", 11));
        ss.add(new S("Dec", 12));

        var tree = SearchTreeAlg.createSearchTree(ss);

        var code = SearchTreeAlg.generateSearchTreeCode(tree, 0);


        var x = 1;


        //
    }
//
//    @Test void g() {
//        var value = createBS("message%3D%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82%20%D1%81%D0%B5%D0%BC%D1%8C%D0%B5%21%26id%3D12323");
//        var y = TOKEN_PERCENT_ENCODED(value, new Buffer());
//
//        var x = 1;
//    }
//
//    @Test void he() {
//
//    }
//
//    interface internalapi.CheetahApi {
//        // после нового read ссылка на старый MemorySegment больше не твоя
//        // после write ссылка на старый MemorySegment, полученный через getWriteBuffer
//        // больше не твой
//
//        // null - end of stream
//        @Nullable MemorySegment read();
//        MemorySegment getWriteBuffer();
//        void write(MemorySegment buffer);
//    }
//
//    interface TrueServiceApi {
//        /**
//         * @return true if connection still alive else false
//         */
//        boolean handleHttpRequest(internalapi.CheetahApi cheetah);
//    }
//
//    // cheetah <- transfer encoding <- gzip <- json
//
//    public interface MemorySegmentInputStream {
//        // null - end of stream
//        @Nullable MemorySegment read();
//    }
//
//    class GzipDecoder implements MemorySegmentInputStream {
//        private final MemorySegmentInputStream parent;
//        private final MemorySegment buffer;
//
//        public GzipDecoder(
//            MemorySegmentInputStream parent,
//            MemorySegment buffer
//        ) {
//            this.parent = parent;
//            this.buffer = buffer;
//        }
//
//        @Override public MemorySegment read() {
//            var data = parent.read();
//            // gzip routine: data -> buffer
//            // return buffer
//            return null;
//        }
//    }
//
//    // byte lookahead(bs, bfr, index) {
//    //     // bs.index  = 1
//    //     // bs.length = 1
//    //
//    //     if (!bfr.isEmpty) {
//    //         return bfr[index]
//    //     }
//    //     else if (bs.length < index) {
//    //         bfr.push()
//    //         return bfr[index]
//    //     }
//    //     else bs[index]
//    // }
//
//    public void d(RequestByteStream bs, Buffer bfr) {
//        // from...
//        // HTTP 1.1\r\n
//        // HOST: ....
//        // bfr.reset()
//        // lookahead(bs, bfr, 4)
//
//        switch ((bs.lookahead(4) & ((byte) 3 << 3)) >>> 3) {
//            case 0:
//                for (var b: "POST pupupu".getBytes(StandardCharsets.UTF_8)) {
//                    if (bs.advance() != b) {
//                        // response 404
//                    }
//                }
//                pupupu(bs);
//                break;
//            case 1:
//                for (var b: "GET hello".getBytes(StandardCharsets.UTF_8)) {
//                    if (bs.advance() != b) {
//                        // response 404
//                    }
//                }
//                hello(bs);
//                break;
//            case 2:
//                for (var b: "GET privet".getBytes(StandardCharsets.UTF_8)) {
//                    if (bs.advance() != b) {
//                        // response 404
//                    }
//                }
//                privet(bs);
//                break;
//            case 3:
//                // not found 404
//                break;
//        }
//    }

}
