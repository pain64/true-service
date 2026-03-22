package http.header;


import http.header.algorithms.Accept.AcceptEncodingParserEncoder;
import http.header.algorithms.Accept.AcceptLanguageParserEncoder;
import http.header.algorithms.Accept.AcceptParserEncoder;
import http.header.algorithms.Accept.AcceptRangesParserEncoder;
import http.header.algorithms.Auth.AuthenticationInfoParserEncoder;
import http.header.algorithms.Auth.AuthorizationParserEncoder;
import http.header.algorithms.Auth.ProxyAuthenticateParserEncoder;
import http.header.algorithms.Auth.ProxyAuthenticationInfoParserEncoder;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.JumpTables.IS_DELIMITER_TABLE;
import static http.JumpTables.IS_TCHAR_TABLE;
import static http.header.DTOs.*;

import http.header.algorithms.*;

public class Decode {
    public static ByteStream createBS(String value) {
        return new ByteStream((value + "\n").getBytes(StandardCharsets.UTF_8));
    }

    @Test void acceptEncoding() {
        var dest = new ArrayList<EncodingWithWeight>();
        var parser = new AcceptEncodingParserEncoder();
        parser.decode(createBS("identity, deflate, gzip;q=1.0, *;q=0.5"), new Buffer(), dest);
        parser.decode(createBS(""), new Buffer(), dest);
        IO.println(dest);
        var x = parser.create(dest);
        var y = 1;
    }

    @Test void acceptLanguage() {
        var dest = new ArrayList<LanguageRangeWithWeight>();
        var parser = new AcceptLanguageParserEncoder();
        parser.decode(createBS("fr-CH, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5"), new Buffer(), dest);
        parser.decode(createBS(""), new Buffer(), dest);
        IO.println(dest);
        var x = parser.create(dest);
        var y = 1;
    }

    @Test void accept() {
        var dest = new ArrayList<MediaRange>();
        var parser = new AcceptParserEncoder();
        parser.decode(createBS("text/html, application/xhtml+xml, application/xml;q=0.9, image/webp, */*;q=0.8"), new Buffer(), dest);
        parser.decode(createBS("text/*;param=param"), new Buffer(), dest);
        parser.decode(createBS("text/html;param=\"pa\\ ram\""), new Buffer(), dest);
        parser.decode(createBS(""), new Buffer(), dest);
        IO.println(dest);
        var x = parser.create(dest);
        var y = 1;
    }

    @Test void acceptRanges() {
        var parser = new AcceptRangesParserEncoder();
        var x = parser.decode(createBS("bytes"), new Buffer());
        var y = 1;
    }

    @Test void authenticationInfo() {
        var dest = new ArrayList<AuthParam>();
        var parser = new AuthenticationInfoParserEncoder();
        parser.decode(createBS("nextnonce=\"4ee60b\", rspauth=\"3e8f9d1c2a5b4d7e\", qop=auth, cnonce=\"0a4f113b\", nc=00000001"), new Buffer(), dest);

        var x = parser.create(dest);
        var y = 1;
    }

    @Test void authorization() {
        var parser = new AuthorizationParserEncoder();
        var x1 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l"), new Buffer());
        var x2 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l==="), new Buffer());
        var x3 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l="), new Buffer());
        var x4 = parser.decode(createBS("Basic realm=\"Dev\", charset=\"UTF-8\""), new Buffer());
        var x5 = parser.decode(createBS("Digest username=username, realm=\"realm\""), new Buffer());

        var y = 1;
    }

    @Test void proxyAuthenticate() {
        var dest = new ArrayList<Challenge>();
        var parser = new ProxyAuthenticateParserEncoder();
        parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l, Basic YWxhZGRpbjpvcGVuc2VzYW1l"), new Buffer(), dest);
        parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l, Basic realm=\"Dev\", charset=\"UTF-8\""), new Buffer(), dest);

        var x = parser.create(dest);
        var y = 1;
    }


    @Test void proxyAuthenticationInfo() {
        var dest = new ArrayList<AuthParam>();
        var parser = new ProxyAuthenticationInfoParserEncoder();
        parser.decode(createBS("nextnonce=\"4ee60b\", rspauth=\"3e8f9d1c2a5b4d7e\", qop=auth, cnonce=\"0a4f113b\", nc=00000001"), new Buffer(), dest);

        var x = parser.create(dest);
        var y = 1;
    }

    @Test void proxyAuthorization() {

    }

    @Test void WWWAuthenticate() {

    }

    @Test void ifMatch() {

    }

    @Test void ifModifiedSince() {

    }

    @Test void ifNoneMatch() {

    }

    @Test void ifRange() {

    }

    @Test void ifUnmodifiedSince() {

    }

    @Test void contentEncoding() {

    }

    @Test void contentLength() {

    }

    @Test void contentLocation() {

    }

    @Test void contentRange() {

    }

    @Test void contentType() {

    }

    //todo COOKIE

    @Test void accessControlAllowCredentials() {

    }

    @Test void accessControlAllowHeaders() {

    }

    @Test void accessControlAllowMethods() {

    }

    @Test void accessControlAllowOrigin() {

    }

    @Test void accessControlExposeHeaders() {

    }

    @Test void accessControlMaxAge() {

    }

    @Test void accessControlRequestHeaders() {

    }

    @Test void accessControlRequestMethod() {

    }

    @Test void TE() {

    }

    @Test void trailer() {

    }

    @Test void allow() {

    }

    @Test void connection() {
        var bs2 = createBS("keep-alive, hello");
        var dest = new ArrayList<String>();
        var parser = new ConnectionParserEncoder();
        parser.decode(bs2, new Buffer(), dest);
        IO.println(dest);
        var x = parser.create(dest);
        var y = 1;
    }

    @Test void date() {

    }


    @Test void ETag() {

    }

    @Test void expect() {

    }

    @Test void lastModified() {

    }

    @Test void maxForwards() {
        var bs = createBS("12323423");
        var parser = new MaxForwardsParserEncoder();
        var x = parser.decode(bs, new Buffer());

        var y = 1;
    }

    @Test void range() {

    }

    @Test void retryAfter() {

    }

    @Test void server() {

    }

    @Test void upgrade() {

    }

    @Test void userAgent() {

    }

    @Test void vary() {

    }

    @Test void via() {

    }

}
