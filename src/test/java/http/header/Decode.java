package http.header;


import http.header.algorithms.Accept.AcceptEncodingParserEncoder;
import http.header.algorithms.Accept.AcceptLanguageParserEncoder;
import http.header.algorithms.Accept.AcceptParserEncoder;
import http.header.algorithms.Accept.AcceptRangesParserEncoder;
import http.header.algorithms.Auth.*;
import http.header.algorithms.CORS.*;
import http.header.algorithms.Conditional.IfMatchParserEncoder;
import http.header.algorithms.Conditional.IfModifiedSinceParserEncoder;
import http.header.algorithms.Conditional.IfRangeParserEncoder;
import http.header.algorithms.Conditional.IfUnmodifiedSinceParserEncoder;
import http.header.algorithms.Content.ContentLengthParserEncoder;
import http.header.algorithms.Content.ContentRangeParserEncoder;
import http.header.algorithms.Content.ContentTypeParserEncoder;
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
        var parser = new ProxyAuthorizationParserEncoder();
        var x1 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l"), new Buffer());
        var x2 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l==="), new Buffer());
        var x3 = parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l="), new Buffer());
        var x4 = parser.decode(createBS("Basic realm=\"Dev\", charset=\"UTF-8\""), new Buffer());
        var x5 = parser.decode(createBS("Digest username=username, realm=\"realm\""), new Buffer());

        var y = 1;

    }

    @Test void WWWAuthenticate() {
        var dest = new ArrayList<Challenge>();
        var parser = new WWWAuthenticateParserEncoder();
        parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l, Basic YWxhZGRpbjpvcGVuc2VzYW1l"), new Buffer(), dest);
        parser.decode(createBS("Basic YWxhZGRpbjpvcGVuc2VzYW1l, Basic realm=\"Dev\", charset=\"UTF-8\""), new Buffer(), dest);

        var x = parser.create(dest);
        var y = 1;
    }

    @Test void ifMatch() {
        var parser = new IfMatchParserEncoder();
        var x1 = parser.decode(createBS("*"), new Buffer());
        var x2 = parser.decode(createBS(""), new Buffer());
        var x3 = parser.decode(createBS("\"67ab43\", W/\"54ed21\", \"7892dd\""), new Buffer());
    }

    @Test void ifModifiedSince() {
        var parser = new IfModifiedSinceParserEncoder();
        var x1 = parser.decode(createBS("Wed, 21 Oct 2015 07:28:00 GMT"), new Buffer());
        var y = 1;
    }

    @Test void ifNoneMatch() {
        var parser = new IfMatchParserEncoder();
        var x1 = parser.decode(createBS("*"), new Buffer());
        var x2 = parser.decode(createBS(""), new Buffer());
        var x3 = parser.decode(createBS("\"67ab43\", W/\"54ed21\", \"7892dd\""), new Buffer());
    }

    @Test void ifRange() {
        var parser = new IfRangeParserEncoder();
        var x1 = parser.decode(createBS("Wed, 21 Oct 2015 07:28:00 GMT"), new Buffer());
        var x2 = parser.decode(createBS("Sun, 21 Oct 2015 07:28:00 GMT"), new Buffer());
        var x3 = parser.decode(createBS("W/\"54ed21\""), new Buffer());
        var x4 = parser.decode(createBS("\"54ed21\""), new Buffer());

        var y = 1;
    }

    @Test void ifUnmodifiedSince() {
        var parser = new IfUnmodifiedSinceParserEncoder();
        var x1 = parser.decode(createBS("Wed, 21 Oct 2015 07:28:00 GMT"), new Buffer());
        var y = 1;
    }

    @Test void contentEncoding() {

    }

    @Test void contentLength() {
        var parser = new ContentLengthParserEncoder();
        var x1 = parser.decode(createBS("123"), new Buffer());
        var y = 1;
    }

    @Test void contentLocation() {
        //TODO
    }

    @Test void contentRange() {
        var parser = new ContentRangeParserEncoder();
        var x1 = parser.decode(createBS("bytes */12424"), new Buffer());
        var x2 = parser.decode(createBS("bytes 0-1233/12424"), new Buffer());
        var x3 = parser.decode(createBS("bytes 341-343/*"), new Buffer());

        var y = 1;
    }

    @Test void contentType() {
        var parser = new ContentTypeParserEncoder();
        var x1 = parser.decode(createBS("text/html"), new Buffer());
        var x2 = parser.decode(createBS("multipart/form-data; boundary=ExampleBoundaryString"), new Buffer());

        var y = 1;
    }

    //todo COOKIE

    @Test void accessControlAllowCredentials() {
        var parser = new AccessControlAllowCredentialsParser();
        var x1 = parser.decode(createBS("true"), new Buffer());

        var y = 1;
    }

    @Test void accessControlAllowHeaders() {
        var parser = new AccessControlAllowHeadersParser();
        var dest = new ArrayList<String>();

        parser.decode(createBS("Accept, Content-Length"), new Buffer(), dest);
        parser.decode(createBS("Content-Type"), new Buffer(), dest);

        var y = 1;
    }

    @Test void accessControlAllowMethods() {
        var parser = new AccessControlAllowMethodsParser();
        var dest = new ArrayList<Method>();

        parser.decode(createBS("GET, POST"), new Buffer(), dest);
        parser.decode(createBS("PUT"), new Buffer(), dest);

        var y = 1;
    }

    @Test void accessControlAllowOrigin() {
        //TODO
    }

    @Test void accessControlExposeHeaders() {
        var parser = new AccessControlExposeHeadersParser();
        var dest = new ArrayList<String>();

        parser.decode(createBS("Content-Encoding, Kuma-Revision"), new Buffer(), dest);
        parser.decode(createBS("*"), new Buffer(), dest);

        var y = 1;
    }

    @Test void accessControlMaxAge() {
//        var parser = new AccessControlMaxAgeParser();
//        var dest = new ArrayList<String>();
//
//        parser.decode(createBS("Content-Encoding, Kuma-Revision"), new Buffer(), dest);
//        parser.decode(createBS("*"), new Buffer(), dest);
//
//        var y = 1;
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
