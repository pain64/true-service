package http;

import java.util.function.Function;

public class Dsl3 {
    // У каждого обработчика
    //   - http method
    //   - route (function name)
    //   - параметры - именованный список {name => Type}
    //   - {r...} = invocation?(p...)
    //   - r = apiInstance.methodName(,,,); r...
    //   - result (return value)
    //      - как scalar {Type}
    //         - statusCode(511)
    //         - toHeader vs toBody(application/json)
    //      - как именованный список {name => Type}
    //         - statusCode(511)
    //         - toHeader(fieldName) vs toBody(fieldName, application/json)
    //      - union by status code <scalar | именованный список>
    //
    //  parameters: from {uri, uriPath, uriQuery, pathParameter, queryParameter, body, getCookie, header, body }
    //  result: to {
    //      statusCode,
    //      header< {HeaderName, ParameterType} => HeaderEncode >,
    //          HeaderEncode это compile-time code-generator
    //      body,
    //      setCookie,
    //      contentEncoding<enum>
    //  }
    // result: List<String> => Body <application/json>
    //     f: ApplicationJsonEncode
    //     "block of code" = f(List<String>)
    //
    // Middleware: compile-time function
    //   f(handlers1) => handlers2
    // f(g(x))

    // String hello() { return "hello" }
    //
    // ClassDef.forMethod("hello", MethodDef
    //     .result(ResultScalar
    //         .path("/")
    //         .httpMethod(GET)
    //         .toBody('text/plain')
    //         .statusCode(201)
    //     )
    // )
    //
    // with CorsMiddleware
    //
    // GET /
    //
    // OPTIONS /

    // ClassDef.forMethod("hello", MethodDef
    //     .result(ResultScalar
    //         .path("/")
    //         .httpMethod(GET)
    //         .toBody('text/plain')
    //         .statusCode(201)
    //     )
    // )

    // ClassDef.forMethod("hello", MethodDef
    //     .result(ResultScalar
    //         .path("/")
    //         .httpMethod(OPTIONS)
    //         .statusCode(200)
    //     )
    // )

    // String hello() { return "hello" }
    // with AuthMiddleware

    // ClassDef.forMethod("hello", MethodDef
    //     .parameterFromCookie("auth", "JSESSIONID") // typeOf<parameter> = Long
    //     .result(ResultScalar
    //         .path("/")
    //         .httpMethod(GET)
    //         .toBody('text/plain')
    //         .statusCode(201)
    //     )
    // )
    // new ClassDef(
    //     new MethodDef("hello")
    //         .parameterFromCookie("auth", "JSESSIONID")
    //         .resultScalar(
    // )
    //

    // ClassDef.
    //     method("hello")
    //         .parameterFromCookie("auth", "JSESSIONID")
    //         .resultScalar()
    //             .path("/")
    //             .httpMethod(GET)
    //             .toBody("text/plain")
    //             .statusCode(201)
    //     .method("bar")

    // forMethod("hello", M
    //     .parameterFromCookie("auth", "JSESSIONID") // typeOf<parameter> = Long
    //     .resultScalar(RS
    //         .path("/")
    //         .httpMethod(GET)
    //         .toBody('text/plain')
    //         .statusCode(201)
    //     )
    // )

    interface DEF { }

    public static class ClassDef {
        MethodDef method(String name) {
            return new MethodDef();
        }

    }

    public static class ResultDef {
        public ResultDef toBody(String mimeType) { return this; }
        public ResultDef statusCode(int code) { return this; }
        public MethodDef end() { return new MethodDef(); }
    }

    public static class MethodDef {
        MethodDef parameterFromCookie(String a, String b) { return this; }
        MethodDef httpPath(String path) { return this; }
        MethodDef httpMethod(String name) { return this; }

        public ResultDef returnScalar() {
            return new ResultDef();
        }

        public ClassDef end() { return new ClassDef(); }
    }

    static final ClassDef DEF = new ClassDef()
        .method("hello")
        /**/.httpPath("/")
        /**/.httpMethod("GET")
        /**/.parameterFromCookie("auth", "JSESSIONID")
        /**/.returnScalar()
        /*    */.toBody("application/json")
        /*    */.statusCode(201).end().end()
        .method("doTheJob")
        /**/.parameterFromCookie("auth", "JSESSIONID").end();

    class Deff {
        Deff path(String path) { return this; }
        Deff toBody(String mimeType) { return this; }
    }

    class Def {
        Def resultScalar(Function<Deff, Deff> build) { return this; }
    }
    static void forMethod(String methodName, Function<Def, Def> build) {

    }

    void xxx() {
        forMethod("hello", l ->
            l.resultScalar(ll -> ll
                .path("/")
                .toBody("application/json")
            )
        );
    }
}
