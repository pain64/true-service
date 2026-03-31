package http;

import java.util.UUID;

// Dsl2.hello
public class Dsl2 {
    // Content-Encoding
    // Загрузка и выгрузка файлов

    @interface HttpMethod {
        String value();
    }

    @HttpMethod("GET") @interface Get {
        String value() default "__magic__true-service_not_defined";
    }
    @HttpMethod("POST") @interface Post {
        String value() default "__magic__true-service_not_defined";
    }
    @HttpMethod("DELETE") @interface Delete {
        String value() default "__magic__true-service_not_defined";
    }
    @HttpMethod("X-My") @interface XMy {
        String value() default "__magic__true-service_not_defined";
    }

    @interface UriPathRoute {}
    @interface UriPathParameters {}
    @interface UriQuery {}
    @interface PathParameter { }
    @interface QueryParameter { }

    @interface MimeType {
        String type();
        String subtype();
    }

    @interface Header { }

    @MimeType(type = "multipart", subtype = "form-data") @interface MultipartFormData { }
    @MimeType(type = "application", subtype = "json") @interface ApplicationJson { }
    @MimeType(type = "text", subtype = "plain") @interface TextPlain { }

    @interface StatusCode {
        int code();
        String reasonPhrase();
    }

    @StatusCode(code = 200, reasonPhrase = "OK") @interface Sc200 { }
    @StatusCode(code = 400, reasonPhrase = "Bad request") @interface Sc400 { }
    @StatusCode(code = 500, reasonPhrase = "Internal server error") @interface Sc500 { }
    @StatusCode(code = 511, reasonPhrase = "Application logic error") @interface Sc511 { }


    // Headers
    record Authorization() { }
    record SetCookie<P>() {
        SetCookie<P> setDomain() { return this; }
        SetCookie<P> setHttpOnly() { return this; }
    }

    @interface CookiePart {
        String value(); // name
    }

    @CookiePart("JSESSIONID") record JSessionId(long id) {}

    String endpoint1(int a, int b) {
        return "";
    }

    // route, method
    // @Get, @Post,
    // @Route("/")


    // Union2<
    //     @Sc200 @TextPlain String,
    //     @Sc511 @TextPlain String
    // > endpoint() {
    //     if (!condition) return Union2.of2("an error occurred")
    //     return Union2.of1("hello");
    // }

    // @Route("XXX", path="/")
    // new SetCookie(new JSessionId(12312))
    //     .setDomain(...)
    //     .setHttpOnly(...)

    @Get("/") @Sc400 @TextPlain String hello() {
        return "hello";
    }

    // new TrueService()
    //     .with(new CorsMiddleware(), H
    //         .http(new MyApi())
    //     )
    //

    // 1. GET /
    //    OPTIONS / => CORS POLICY (HEADERS)
    // 2. GET /

    @Sc400 @TextPlain String endpoint1(
        @QueryParameter UUID id,
        @Header Authorization authorization,
        @MultipartFormData int a,
        @MultipartFormData int b,
        @Header JSessionId sessionId
    ) {
        return "";
    }
}
