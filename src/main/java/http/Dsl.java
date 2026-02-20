package http;

import net.truej.service.union.Union2;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Dsl {
    // TODO: all well-known headers
    // TODO: spec byStatusCode by 4
    // TODO: spec SetCookie by 8
    // request
    //   - method
    //   - uri
    //     - Path
    //        - PathParameter[i]  // /a/b
    //     - Query
    //        - QueryParameter[name] // a=5&b=7
    //   - headers
    //   - body

    public static class Uri { }
    public static class Path { }
    public static class Query { }

    public static class PathParameter<T> {
        public final T v;
        public PathParameter(T v) { this.v = v; }
    }
    public static class QueryParameter<T> {
        public final T v;
        public QueryParameter(T v) { this.v = v; }
    }


    interface HttpHeader { }
    interface HttpRequestHeader { } // ???
    interface HttpResponseHeader { } // ???

    //https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers

    //Authorization
    public static class Authorization implements HttpHeader { }
    public static class ProxyAuthorization implements HttpHeader { }

    //Connection
    public static class Connection implements HttpHeader { }
    public static class KeepAlive implements HttpHeader { }


    public static class Accept implements HttpHeader { }
    public static class AcceptEncoding implements HttpHeader { }
    public static class AcceptCharset implements HttpHeader { }
    public static class AcceptLanguage implements HttpHeader { }
    public static class AcceptRange implements HttpHeader { }

    public static class MaxForwards implements HttpHeader { }

    //CORS

    //Downloads
    public static class ContentDisposition implements HttpHeader { }

    //Message body information
    public static class ContentLength implements HttpHeader { }
    public static class ContentType implements HttpHeader { }
    public static class ContentEncoding implements HttpHeader { }
    public static class ContentLanguage implements HttpHeader { }
    public static class ContentLocation implements HttpHeader { }

    //Redirects
    public static class Location implements HttpHeader { }
    public static class Refresh implements HttpHeader { }

    //Request context
    public static class From implements HttpHeader { }
    public static class Host implements HttpHeader { }
    public static class Referer implements HttpHeader { }
    public static class ReferrerPolicy implements HttpHeader { }
    public static class UserAgent implements HttpHeader { }

    //Cache-control
    public static class CacheControl implements HttpHeader { }


    //Date
    public static class Date implements HttpHeader { }

    public static class Server implements HttpHeader { }

    public interface CookieHeader extends HttpHeader { } // kv
    public static class SetCookie1<T> implements HttpHeader { }
    public static class SetCookie2<T1, T2> implements HttpHeader { }
    public static class SetCookie3<T1, T2, T3> implements HttpHeader { }
    public static class SetCookie4<T1, T2, T3, T4> implements HttpHeader { }
    public static class SetCookie5<T1, T2, T3, T4, T5> implements HttpHeader { }
    public static class SetCookie6<T1, T2, T3, T4, T5, T6> implements HttpHeader { }
    public static class SetCookie7<T1, T2, T3, T4, T5, T6, T7> implements HttpHeader { }
    public static class SetCookie8<T1, T2, T3, T4, T5, T6, T7, T8> implements HttpHeader { }

    sealed interface Headers { }


    public static final class H0 implements Headers { }
    public static final class H1<T1 extends HttpHeader> implements Headers { }
    // Set-Cookie: my-parameter=42
    @Target(ElementType.TYPE_USE) public @interface Name {
        String value();
    }

    public interface CookiePart<T> { }

    public static class MyCookiePart1 implements @Name("my-part-1") CookiePart<String> { }
    public static class MyCookiePart2 implements @Name("my-part-2") CookiePart<Integer> { }

    public static final class H2<
        T1 extends HttpHeader,
        T2 extends HttpHeader
        > implements Headers { }

    public static final class H3<
        T1 extends HttpHeader,
        T2 extends HttpHeader,
        T3 extends HttpHeader
        > implements Headers { }

    interface MimeMessage { }
    public static class ApplicationJson<T> implements MimeMessage { }
    public static class TextPlain implements MimeMessage {
        public TextPlain(String text) { }
    }

    interface StatusCode { }
    public static class ScOk implements StatusCode { }
    public static class ScBadRequest implements StatusCode { }
    public static class ScInternalServerError implements StatusCode {}

    public sealed interface ByStatusCode2<
        R1 extends HttpResponse<?, ?, ?>,
        R2 extends HttpResponse<?, ?, ?>
        > {

        record V1<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>
            >(R1 value) implements ByStatusCode2<R1, R2> { }

        record V2<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>
            >(R2 value) implements ByStatusCode2<R1, R2> { }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>> ByStatusCode2<R1, R2> of1(R1 value) {
            return new ByStatusCode2.V1<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>> ByStatusCode2<R1, R2> of2(R2 value) {
            return new ByStatusCode2.V2<>(value);

        }
    }

    public sealed interface ByStatusCode3<
        R1 extends HttpResponse<?, ?, ?>,
        R2 extends HttpResponse<?, ?, ?>,
        R3 extends HttpResponse<?, ?, ?>
        > {

        record V1<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>
            >(R1 value) implements ByStatusCode3<R1, R2, R3> { }

        record V2<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>
            >(R2 value) implements ByStatusCode3<R1, R2, R3> { }

        record V3<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>
            >(R3 value) implements ByStatusCode3<R1, R2, R3> { }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>> ByStatusCode3<R1, R2, R3> of1(R1 value) {
            return new ByStatusCode3.V1<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>> ByStatusCode3<R1, R2, R3> of2(R2 value) {
            return new ByStatusCode3.V2<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>> ByStatusCode3<R1, R2, R3> of3(R3 value) {
            return new ByStatusCode3.V3<>(value);
        }
    }


    public sealed interface ByStatusCode4<
        R1 extends HttpResponse<?, ?, ?>,
        R2 extends HttpResponse<?, ?, ?>,
        R3 extends HttpResponse<?, ?, ?>,
        R4 extends HttpResponse<?, ?, ?>
        > {

        record V1<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>
            >(R1 value) implements ByStatusCode4<R1, R2, R3, R4> { }

        record V2<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>
            >(R2 value) implements ByStatusCode4<R1, R2, R3, R4> { }

        record V3<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>
            >(R3 value) implements ByStatusCode4<R1, R2, R3, R4> { }

        record V4<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>
            >(R4 value) implements ByStatusCode4<R1, R2, R3, R4> { }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>> ByStatusCode4<R1, R2, R3, R4> of1(R1 value) {
            return new ByStatusCode4.V1<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>> ByStatusCode4<R1, R2, R3, R4> of2(R2 value) {
            return new ByStatusCode4.V2<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>> ByStatusCode4<R1, R2, R3, R4> of3(R3 value) {
            return new ByStatusCode4.V3<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>> ByStatusCode4<R1, R2, R3, R4> of4(R4 value) {
            return new ByStatusCode4.V4<>(value);
        }
    }


    public static class HttpResponse<
        S extends StatusCode,
        H extends Headers,
        B extends MimeMessage
        > {

        public HttpResponse(S s, H h, B B) {

        }
    }

    //public static class HttpRequest { }

    // POST Dsl.add/{a}
    // body: { "b": 2 }
    // response: json<int>
    public int add(
        PathParameter<Integer> a, int b
    ) { return a.v + b; }


    public record Vec2f(float x, float y) { }

    public ByStatusCode2<
        HttpResponse<
            ScOk, H2<Location, SetCookie2<MyCookiePart1, MyCookiePart2>>,
            ApplicationJson<Vec2f>>,
        HttpResponse<ScBadRequest, H0, TextPlain>>

    doSomething(
        // Dsl.doSomething/{departmentId}/{userId}/?userName=...&age=...
        PathParameter<UUID> departmentId,
        PathParameter<UUID> userId,
        QueryParameter<String> userName,
        QueryParameter<Integer> age,
        Uri uri, Path path, Query query,
        ContentLanguage contentLanguage,
        ApplicationJson<Vec2f> vector
    ) {
        if (age.v < 18)
            return ByStatusCode2.of2(
                new HttpResponse<>(new ScBadRequest(), new H0(), new TextPlain("too small"))
            );

        return null;
    }

    public interface NextExtra1<T> {
        void next(T extraParameter1);
    }

    public record Authorization() {}

    public ByStatusCode2<
        HttpResponse<?, ?, ?>,
        HttpResponse<ScInternalServerError, H0, TextPlain>
        >
    handle(NextExtra1<Authorization> next) {
        // имеет ли middleware требования к тому что вернет decoratee?
        //
        try {
            next.next(new Authorization());
        } catch (Exception e) {
            return ByStatusCode2.of2(
                // Sc500 Sc200 Sc307
                new HttpResponse<>(new ScInternalServerError(), new H0(), new TextPlain("too small"))
            );
        }
        return null;
    }

    // Middleware design
    // class ExceptionHandlingMiddleware {
    //     ByStatusCode2<
    //         >
    //     handle(Runnable invocation) {
    //         try {
    //             return of1(invocation.run());
    //         } catch(Exception e) {
    //             return
    //         }
    //     }
    // }
}
