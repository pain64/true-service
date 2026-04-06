package http;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.util.UUID;

import static http.Dsl2.ContentEncoders.*;

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

    @interface UriPathRoute { }
    @interface UriPathParameters { }
    @interface UriQuery { }
    @interface PathParameter { }
    @interface QueryParameter { }

    @interface MimeType {
        String type();
        String subtype();
    }

    @interface Header { }

    @MimeType(type = "multipart", subtype = "form-data") @interface MultipartFormData { }
    @MimeType(type = "application", subtype = "json") @interface ApplicationJson { }
    @Target(ElementType.TYPE_USE) @MimeType(type = "text", subtype = "plain")
    @interface TextPlain { }

    @interface StatusCode {
        int code();
        String reasonPhrase();
    }

    @StatusCode(code = 200, reasonPhrase = "OK") @interface Sc200 { }
    @Target(ElementType.TYPE_USE) @StatusCode(code = 400, reasonPhrase = "Bad request")
    @interface Sc400 { }
    @StatusCode(code = 500, reasonPhrase = "Internal server error") @interface Sc500 { }
    @StatusCode(code = 511, reasonPhrase = "Application logic error") @interface Sc511 { }


    // Headers
    record Authorization() { }
    record SetCookie<P>() {
        SetCookie<P> setDomain() { return this; }
        SetCookie<P> setHttpOnly() { return this; }
    }

    @interface GetCookie {
        String value(); // name
    }

    @interface Middleware { }

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
    //     .with(
    //         new CorsMiddleware(), H
    //             .http(new MyApi())
    //     )
    // new TrueService()
    //     .<AuthRequiredException, @Sc400 @TextPlain String> exceptionMapping(
    //         ex -> "authentication required"
    //     )
    //     .<Exception, @Sc511 @TextPlain String> exceptionMapping(
    //         ex -> ex.getMessage()
    //     )
    //     .with(
    //         List.of(
    //             new CorsMiddleware(),
    //             new AuthMiddleware(
    //                 Env.getString("AUTH_API_TOKEN")
    //             )
    //         ), H
    //             .http(new MyApi())
    //     )

    // -- FileDownload.java
    // public record Config(Path sourceDirectory) {}
    //
    // public static final Module<Config> DEF = cfg -> ModuleDef
    //     .with(
    //         new CorsMiddleware(), WithDef
    //             .http(new FileDownloadApi(cfg.sourceDirectory));
    //     )
    // -- Main.java
    // TrueHttpDef
    //     .module(FileDownload.DEF.instance(new Config("/data"))
    //     .http(new UrlShortener()) // DEF is implicitly resolved

    // public class UrlShortener {
    //     public static ClassDef DEF = ClassDef
    //         .forMethod("doShort", MethodDef
    //             .httpMethod(GET)
    //             .parameterFromPath("id")
    //             .result(ResultSingleDef
    //                 .toHeader("Location")
    //                 .statusCode(301)
    //             ) // vs
    //             .result(ResultStructDef
    //                 .toLocation("f1")
    //             ) // vs
    //             .result(ResultUnionDef
    //                 .variant(0, 200, ResultStructDef
    //                     .toCookie(0, "JSESSIONID")
    //                     .toBody(1, "application/json")
    //                 )
    //                 .variant(1, 511, ResultSingleDef
    //                     .toBody("text/plain")
    //                 )
    //             )
    //         )
    //
    //     public String doShort(String id) {
    //         return findShortById(id);
    //     }
    // }

    // class MyModule implements ModuleDef {
    //     @Override define() {
    //         return new
    //     }
    // }

    // 1. GET /
    //    OPTIONS / => CORS POLICY (HEADERS)
    // 2. GET /
    // 0. CorsMiddleware
    // 1. ContentEncodingMiddleware
    // 2. AddParameterMiddleware
    // 3. AddResultMiddleware
    // 4. BeforeAfterMiddleware
    //
    // interface AddParameterMiddleware<T> {
    //     String extraParameterName() { return "userId" }
    //     T extraParameter(@Header JSessionId sessionId) {
    //
    //     }
    // }
    // interface AddResultMiddleware {
    //     void extraResult(@Middleware
    // }

    // FilterChain vs ???

    // @Get("/") Union2<
    //     @Sc400 List2<
    //         @TextPlain String  ,
    //         @Header    MyHeader
    //     >,
    //     @Sc500 @TextPlain String
    // > endpoint1(@Header JSessionId sessionId, int a, int b) {
    //
    // }

//    return ds.q("""
//            select
//                id,
//                name
//            from users_tag
//            where task = ? and not is_archived
//        """, taskType
//    ).g.<AllowedTag> fetchList();
    //
    // ds.q("select 1").<@Nullable Integer> fetchOne();
    // ds.q("select * from users").g.<User> fetchList();
    // ds.q("select * from users").g.fetchList(User.class);
    // ds.q("delete from users where id = ?", 42).execute();
    //

//    record BotAvailabilityFields (
//        int tagId,
//        boolean isHidden
//    ) {}
//
//    public void changeTagBotAvailability(int tagId, boolean isHidden) {
//        ds.asGeneratedKeys("id").g.<User> fetchOne(
//            "update users_tag set is_hidden_in_bot = ? where id = ?",
//            isHidden, tagId
//        );
//    }

//    record BotAvailabilityFields (
//        int tagId,
//        boolean isHidden
//    ) {}
//
//    public void changeTagBotAvailability(int tagId, boolean isHidden) {
//        ds.fetchNone(
//            "update users_tag set is_hidden_in_bot = ? where id = ?",
//            f.isHidden, f.tagId
//        );
//    }

//  changeTagBotAvailability = function tagId, isHidden ->
//      fetchList< G<User> >⟩ ds, asGeneratedKeys⟩ ['id', 'name'] , q⟩
//          'select * from users where id > ?', 100
//      nil

//      ds.q("select * from users where id > ?", 100).asGeneratedKeys( fetchList(
//          asGeneratedKeys("select * from users where id > ?"), 100
//      )

//   var keys = cn.batch(discounts).withUpdateCount.fetchNone(
//       "update bill b set discount = ? where cast(b.date as date) = ?",
//       v -> new Object[]{v.discount, v.date}
//   );


    public static class ContentEncoders {
        public static final String LZ4 = "Lz4";
        public static final String GZIP = "Gzip";
    }

    @interface Encoder {
        String value();
        boolean isAlreadyEncoded() default false;
    }

    @interface ApplicationPdf {
        Encoder[] contentEncoding() default {};
    }


    @Get @ApplicationPdf(
        contentEncoding = {
            @Encoder(value = LZ4, isAlreadyEncoded = true), @Encoder(GZIP)
        }
    ) File downloadPdf(@PathParameter String fileName) {
        return null;
        // TODO
    }


    // List2<
    //     @TextPlain String,
    //     @SetCookie("JSESSIONID") Long
    // >

    @Get("/") @Sc400 @TextPlain String endpoint1(
        @Middleware long userId,
        @QueryParameter UUID id,
        @Header Authorization authorization,
        @MultipartFormData int a,
        @MultipartFormData int b,
        @GetCookie("JSESSIONID") long sessionId
    ) {
        return "";
    }

    interface RemoteService {
        int sum(int a, int b);
    }
}
