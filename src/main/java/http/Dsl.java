package http;

import net.truej.service.union.Union2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.UUID;

public class Dsl {
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

    public static class Location implements HttpHeader { }
    public static class ContentLanguage implements HttpHeader { }
    public interface CookieHeader extends HttpHeader { } // kv
    public static class SetCookie1<T> implements HttpHeader { }
    public static class SetCookie2<T1, T2> implements HttpHeader { }
    public static class SetCookie3<T1, T2, T3> implements HttpHeader { }

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
}
