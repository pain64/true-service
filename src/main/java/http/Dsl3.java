package http;

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
}
