package http;

import lombok.Data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.UUID;

import static http.Dsl.BeforeResult.*;
import static http.Dsl.BeforeResult.ofFailure;

public class Dsl {
    // Расширения:
    //    1. encode / decode для MimeMessage
    //    2. encode / decode для заголовков
    //        2.1 специализация парсера конкретного заголовка. Например, cookie
    //    3. Utf8Bytes encode / decode. PathParameter<T>, QueryParameter<T>, CookieParameter<T>, AuthorizationParameter<T>
    //
    // Эксперименты:
    //    1. Доделать вызов сгенерированного байткода
    //    2. Попробовать после парсера добавлять аннотацию, включающую процессинг аннотаций,
    //        чтобы в нем уже сгенерировать новый код
    // FormMultipart<F>
    // FormMultipartBind().decode(F.class /* type */)
    //     bytecode patches to concrete implementation of F.class encoder
    // FormMultipartBind().encode(fInstance)


    //  @Get("/") Union2<
    //      @Sc400 @TextPlain String,
    //      List2<
    //          @FormMultipart @Name("name") String ,
    //          @FormMultipart @Name("age")  Integer
    //      >
    //  > foo(
    //                      Method method  ,
    //     @Uri             String uri     ,
    //     @PathParameter   int    id      ,
    //     @QueryParameter  String location,
    //     @FormMultipart   String name    ,
    //     @FormMultipart   int    age     ,
    //     @ApplicationJson String other   , -- compilation error: could not mix
    //                                       -- form/multipart and application/json
    // ) {
    //     if (condition)
    //         return Variant2.of1("Bad request");
    //
    //     return Variant2.of2(new List2<>("Lalka", 18));
    // }
    //
    // !!! STARLANG VERSION !!!
    // foo = endpoint(Get, "/", id -> {
    //     id.v -- explicit used-decided invocation of .v on PathParameter
    //          -- id now has PathParameter<?> type
    //     if condition then
    //         return [Sc400, TextPlain('Bad request')]
    //
    //     return [
    //         {'name': FormMultipart('Lalka')},
    //         {'age' : FormMultipart(18)     }
    //     ]
    // })

    // @Get("/oops") String foo(
    //     Method method, Uri uri,
    //     PathParameter<Integer> id,
    //     QueryParameter<String> location,
    //     FormMultipart<String> name,
    //     FormMultipart<Integer> age
    // ) {
    // }

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

    public sealed interface Headers { }


    public static final class H0 implements Headers { }
    public static final class H1<T1 extends HttpHeader> implements Headers { }
    // Set-Cookie: my-parameter=42
    @Target(ElementType.TYPE_USE) public @interface Name {
        String value();
    }

    // SetCookie<JSessionId, CNil>
    // Cookie<JSessionId, CNil>

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

    public interface MimeMessage { }
    @Data public static class ApplicationJson<T> implements MimeMessage {
        public final T v;
    }
    public static class TextPlain implements MimeMessage {
        public TextPlain(String text) { }
    }

    public interface StatusCode { }
    public static class Sc200 implements StatusCode {
        public static Sc200 v = new Sc200();
    }
    public static class Sc400 implements StatusCode {
        public static Sc400 v = new Sc400();
    }
    public static class Sc500 implements StatusCode {
        public static Sc500 v = new Sc500();
    }
    public static class Sc401 implements StatusCode {
        public static Sc401 v = new Sc401();
    }


    public sealed interface Variants2<
        R1 extends HttpResponse<?, ?, ?>,
        R2 extends HttpResponse<?, ?, ?>
        > extends EndpointResult {

        record V1<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>
            >(R1 value) implements Variants2<R1, R2> { }

        record V2<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>
            >(R2 value) implements Variants2<R1, R2> { }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>> Variants2<R1, R2> of1(R1 value) {
            return new Variants2.V1<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>> Variants2<R1, R2> of2(R2 value) {
            return new Variants2.V2<>(value);

        }
    }

    public sealed interface Variants3<
        R1 extends HttpResponse<?, ?, ?>,
        R2 extends HttpResponse<?, ?, ?>,
        R3 extends HttpResponse<?, ?, ?>
        > {

        record V1<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>
            >(R1 value) implements Variants3<R1, R2, R3> { }

        record V2<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>
            >(R2 value) implements Variants3<R1, R2, R3> { }

        record V3<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>
            >(R3 value) implements Variants3<R1, R2, R3> { }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>> Variants3<R1, R2, R3> of1(R1 value) {
            return new Variants3.V1<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>> Variants3<R1, R2, R3> of2(R2 value) {
            return new Variants3.V2<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>> Variants3<R1, R2, R3> of3(R3 value) {
            return new Variants3.V3<>(value);
        }
    }


    public sealed interface Variants4<
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
            >(R1 value) implements Variants4<R1, R2, R3, R4> { }

        record V2<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>
            >(R2 value) implements Variants4<R1, R2, R3, R4> { }

        record V3<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>
            >(R3 value) implements Variants4<R1, R2, R3, R4> { }

        record V4<
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>
            >(R4 value) implements Variants4<R1, R2, R3, R4> { }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>> Variants4<R1, R2, R3, R4> of1(R1 value) {
            return new Variants4.V1<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>> Variants4<R1, R2, R3, R4> of2(R2 value) {
            return new Variants4.V2<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>> Variants4<R1, R2, R3, R4> of3(R3 value) {
            return new Variants4.V3<>(value);
        }

        static <
            R1 extends HttpResponse<?, ?, ?>,
            R2 extends HttpResponse<?, ?, ?>,
            R3 extends HttpResponse<?, ?, ?>,
            R4 extends HttpResponse<?, ?, ?>> Variants4<R1, R2, R3, R4> of4(R4 value) {
            return new Variants4.V4<>(value);
        }
    }

    public sealed interface EndpointResult { }

    public sealed interface HeaderList { }
    public static final class H<X extends HttpHeader, N extends HeaderList> implements HeaderList {
        public final X v;
        public final N next;
        public H(X httpHeader, N next) {
            this.v = httpHeader;
            this.next = next;
        }
    }
    public static final class HNil implements HeaderList {
        public static HNil v = new HNil();
    }


    @Data public final static class HttpResponse<
        S extends StatusCode,
        H extends HeaderList,
        M extends MimeMessage
        > implements EndpointResult {

        public final S statusCode;
        public final H headers;
        public final M body;
    }

    //public static class HttpRequest { }

    // POST Dsl.add/{a}
    // body: { "b": 2 }
    // response: json<int>
    public int add(
        PathParameter<Integer> a, int b
    ) { return a.v + b; }


    public record Vec2f(float x, float y) { }


    public Variants2<
                HttpResponse<
                    Sc200, H<Location, H<SetCookie2<MyCookiePart1, MyCookiePart2>, HNil>>,
                    ApplicationJson<Vec2f>>,
                HttpResponse<Sc400, HNil, TextPlain>>

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
            return Variants2.of2(
                new HttpResponse<>(new Sc400(), HNil.v, new TextPlain("too small"))
            );

        return null;
    }

    public sealed interface BeforeResult<ExtraNext, BeforeFailure extends EndpointResult> {
        record Success<
            ExtraNext, BeforeFailure extends EndpointResult
            >(ExtraNext value) implements BeforeResult<ExtraNext, BeforeFailure> { }

        record Failure<
            ExtraNext, BeforeFailure extends EndpointResult
            >(BeforeFailure value) implements BeforeResult<ExtraNext, BeforeFailure> { }

        static <ExtraNext, BeforeFailure extends EndpointResult> BeforeResult<
            ExtraNext, BeforeFailure> ofSuccess(ExtraNext value) {
            return new Success<>(value);
        }

        static <ExtraNext, BeforeFailure extends EndpointResult> BeforeResult<
            ExtraNext, BeforeFailure> ofFailure(BeforeFailure value) {
            return new Failure<>(value);
        }
    }

    interface Middleware { }

    public static class AuthenticationMiddleware implements Middleware {

        public record User(long id, String name) { }

        public BeforeResult<User,
            HttpResponse<Sc401, HNil, TextPlain>> before(Authorization authorization) {

            if (false) return ofFailure(
                new HttpResponse<>(Sc401.v, HNil.v, new TextPlain("Пользователь не найден"))
            );

            return ofSuccess(new User(42L, "Joe"));
        }

        public <S extends StatusCode, X extends HeaderList,
            M extends MimeMessage> HttpResponse<S, X, M> onAfter1(
            HttpResponse<S, X, M> nextResult
        ) {
            return nextResult; // pass through without conversion
        }
    }

    // Variadic templates ???
    // H<Authorization, H<ContentType, HEnd>>
    //

    public static class ToJsonRpcMiddleware implements Middleware {

        public sealed interface RpcResult<T> { }
        public record Ok<T>(T result) implements RpcResult<T> { }
        public record Fail<T>(String message) implements RpcResult<T> { }

        public BeforeResult<Void, ?> before() {
            return ofSuccess(null); // no extra parameters
        }

        // route by:
        //     1. StatusCode
        //     2. MimeType
        public <X extends HeaderList, T> HttpResponse<Sc200, X,
            ApplicationJson<RpcResult<T>>> onAfter1(
            HttpResponse<Sc200, X, ApplicationJson<T>> nextResult
        ) {
            return new HttpResponse<>(
                Sc200.v, nextResult.headers,
                new ApplicationJson<>(new Ok<>(nextResult.body.v))
            );
        }

        // route by: Exception class
        public HttpResponse<Sc200, HNil, ApplicationJson<Fail<Void>>> onException1(
            Exception nextException
        ) {
            return new HttpResponse<>(
                Sc200.v, HNil.v, new ApplicationJson<>(
                new Fail<>(nextException.getMessage())
            ));
        }
    }


    public static class DropHeaderMiddleware implements Middleware {

        public <S extends StatusCode, X extends HeaderList, C extends MimeMessage
            > HttpResponse<S, X, C> onAfter1(
            HttpResponse<S, H<Authorization, X>, C> nextResult
        ) {
            return new HttpResponse<>(
                nextResult.statusCode, nextResult.headers.next, nextResult.body
            );
        }
    }

    public static class AddHeaderMiddleware implements Middleware {

        public <S extends StatusCode, X extends HeaderList, C extends MimeMessage
            > HttpResponse<S, H<Server, X>, C> onAfter1(
            HttpResponse<S, X, C> nextResult
        ) {
            return new HttpResponse<>(
                nextResult.statusCode, new H<>(new Server(), nextResult.headers), nextResult.body
            );
        }
    }

    // Middleware features:
    //     1. добавление extra параметров в контекст
    //     2. добавление extra логики до и после вызова upstream
    //     3. в зависимости от __того что вернул upstream__ как-то переобработать ответ
    //     4. обработать исключение со стороны upstream
    // function middleware(PathVariable extra, upstream) {
    //     if(!assertSome(extra)) return ???
    //
    // }
    // Разделим middleware на before и after
    //     before:
    //         сделать extra работу до вызова upstream. Если handlable ошибка - какой-то другой статус код???
    //     after:
    //         1. сделать extra работу после вызова upstream
    //         2. Каждый известный тип ответа upstream замапить во что-то другое?
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
