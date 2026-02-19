package org.example.shortener;

import net.truej.service.servlet.*;

import javax.sql.DataSource;

import java.lang.management.ManagementFactory;

import static net.truej.service.xxx.Server.*;

// Normal Http       : Array<Request -> Response>
// Web Socket        : Connector(headers, ch) -> ch.onMessage Variant<MessageIn>, ch.send Variant<MessageOut>
// Server Sent Events: Connector(heders, ch) ->  ch.send VariantMessage<Out>

// class SidebarApi {
//     Auth whoAmI(C<Auth> auth) { return auth.v; }
// }

// public int sum(Stream<Integer> numbers) {
//     var s = 0;
//     for (var i : numbers) s += i;
//     return s;
// }

// LexInput
//     nextByte()
//     mark()
//

//
// JsonLexer(LexInput)
//     curlyBraceClose  ->
//     curlyBraceOpen   ->
//     squareBraceClose ->
//     squareBraceOpen  ->
//     whiteSpace       ->
//     comma            ->
//     doubleQuote      ->
//     memberName       -> String
// JsonParser
//     string  -> JsonString
//     true    -> JsonFalse
//     false   -> JsonTrue
//     null    -> JsonNull
//     number  -> JsonNumber
//     array   -> JsonArray
//     object  -> JsonObject
//     json    -> JsonValue
//
//

// PathVariableReader (UUID | String | Integer | Long | Short | Byte | Boolean and List<Pair<T, s -> T>)
// HttpSessionReader(lambda)
// HeaderReader (String | Integer | Long | Short | Byte | Boolean and optionals as no header)
// JsonBodyReader (any data)
// JsonBodyWriter (any data, 200 as code, auto exception handling as error)
// MultipartReader
// MultipartWriter
// BinaryStream???
// HttpRequestReader() -- untyped
// RedirectWriter(301 | 302) String
// HeaderWriter (String | Integer | Long | Short | Byte | Boolean)
// StatusCodeWriter(401.. + message)
// HttpResponseWriter() -- untyped
//
//
// Reader и Writer это factory функции
//     - генерирующие java-код сервера
//     - генерирующие код клиента
//     - инстанс reader и writer это лишь параметры (const литерал)
//     - будет ли какое-то http описание ???
//     - генераторов клиентского кода может быть много???
//         - typescript + fetch
//         - swagger
//         - генератор должен быть модульным???
//             - JsonBodyReaderTsClientGenerator
//             - JsonBodyWriterTsClientGenerator
//             -

// Http протокол:
//    Request:
//        Path
//        Headers
//        Body - mime type
//        Session ???
//    Response:
//        Headers
//        Status code
//        Body
//        Session ???
// Что такое endpoint ???
//     функция Args -> Result | Exception
//         каждый аргумент имеет тип и имя
//         У каждого аргумента (тип + имя) есть свой резолвер
//         Те аргументы, которые не зарезовлены, попадают default resolver (body)
// service
// method    - in-service configuration
// realm     - grouping
// parameter - eagerly resolved
// defaultParameterReader & defaultResultWriter (server, realm, service)
// parameterReader(pName) & resultWriter -> factory: returns block of code ???
// 
// JsonBodyResultWriter(
//    new TrueJson()
//        .forType(int.class, ... )
// )


public class Main2 {
    // 1. Генератор для одного эндпоинта
    //    Method: name

    public static void main(String[] args) {

        System.out.println("time: " + ManagementFactory.getRuntimeMXBean().getUptime() + "ms");
//        new JsonBodyParametersReader(),
//        new JsonBodyResultWriter(),
        var ds = (DataSource) null;


        // class HttpServletMiddleware implements Middleware {
        //
        //     void process(Context ctx) {
        //         generated code here !!!
        //         generated route here !!!
        //         var a = trueJsonReadParameters(ctx.parameterTypes()) -- defined in true-service library
        //         var r = ctx.next(a)
        //         trueJsonWriteResult(r, ctx.resultType())        -- defined in true-service library
        //     }
        // }
        //
        // ctx -> {
        //     trueJsonReadParameters(ctx.parameterTypes()) -- defined in true-service library
        //     trueJsonWriteResult(ctx.resultType())        -- defined in true-service library
        // }
        //

        // httpServletMiddleware(
        //     service(
        //         new UrlShortenerApi(ds),
        //         method(
        //             "decode", path("/l/"),
        //              pathVariableParameters(),
        //              resultAsRedirect(),
        //             ctx -> {
        //                 var xc = ctx.get("exchange", HttpServletExchange.class);
        //                 var url = ctx.<String>next(new Extra("path", ... get from request path ...);
        //                 xc.response.setStatus(302);
        //                 xc.response.setHeader("Location", url);
        //             }
        //         )
        //     )
        // )

        // new HttpServletMiddleware()
        //     .service(
        //         new UrlShortenerApi(ds), s ->
        //             s.method(
        //                 "decode", m -> m
        //                     .path("/l")
        //                     .pathVariableParameters()
        //                     .resultAsRedirect()
        //             )
        //         )
        //      )

        // http vs json ???
        // httpServletMiddleware(
        //     service(
        //         new UrlShortenerApi(ds),
        //         method(
        //             "decode", path("/l/"),
        //              pathVariableParameters(),
        //              resultAsRedirect(),
        //             ctx -> {
        //                 var xc = ctx.get("exchange", HttpServletExchange.class);
        //                 var url = ctx.<String>next(new Extra("path", ... get from request path ...);
        //                 xc.response.setStatus(302);
        //                 xc.response.setHeader("Location", url);
        //             }
        //         )
        //     )
        // )

// NB: по reader и writer мы должны суметь сгенерировать клиент!!!
//     reader(instance)
//     reader(Type, instance)
//     reader(Type, parameterName, instance)
//     writer(instance)
//     exceptionWriter(Class<? extends Exception>, instance) // bad idea
//     writer(
//         new HeaderWriter("ETag"), // FIXME: header have format ???
//         new BodyApplicationJsonWriter(),
//         new BodyMultipartFormDataWriter(),
//         new BodySinglePartReader("image/png", xc -> { new T() })
//         new BodySinglePartWriter("image/png", t -> { resp.inputStream().write(...) })
//         new StatusCodeWriter()
//             .codes(301, 302),
//
//         new StatusCodeWriter().codes(302).handler(resp -> resp.statusCode(302))
//     ),
//
//
//        var conf = new HttpServletServer(
//            service(
//                new UrlShortenerApi(ds),
//                method(
//                    // http:some.ru/l/1234
//                    "decode", httpMethod(GET), path("/l"),
//                    reader(new PathVariableParametersReader()),
//                    writer(new RedirectResultWriter())
//                )
//            )
//        );

// instance, method, instances, reader, writer
// withInstance, withInstances, instance, forMethod, useReader, useWriter
// http: httpMethod, path
//        var conf = new HttpApi<HttpServletExchange>(
//            instance(
//                new UrlShortenerApi(ds),
//                reader(new JsonRpcReader()),
//                writer(new JsonRpcWriter()),
//                method(
//                    // http:some.ru/l/1234
//                    "decode", httpMethod(GET), path("/l"),
//                    reader(new PathVariableReader()),
//                    writer(new RedirectWriter())
//                )
//            )
//        );

//        var conf = new HttpApi<HttpServletExchange>()
//            .withInstance(
//                new UrlShortenerApi(ds),
//                I.forMethod(
//                    "decode", M
//                     .useHttpMethod(GET)
//                     .usePath("/l")
//                     .useReader(new PathVariableReader())
//                     .useWriter(new RedirectWriter())
//                )
//            )

//        var conf = new HttpApi<HttpServletExchange>()
//            .withInstance(
//                new I(new UrlShortenerApi(ds))
//                   .forMethod(
//                       new M("decode")
//                           .useHttpMethod(GET)
//                           .usePath("/l")
//                           .useReader(new PathVariableReader())
//                           .useWriter(new RedirectWriter())
//                   )
//            )
//                .forMethod(
//                    // http:some.ru/l/1234
//                    "decode", useHttpMethod(GET), usePath("/l"),
//                    useReader(new PathReader()),
//                    useWriter(new RedirectWriter())
//                    forParameter(
//                      "a", useReader(...)
//                    ),
//                    forReturnPart(
//                        "b", useWriter(new AaaW())
//                    ),
//                    forType(Session.class, useReader(
//                        new ExchangeReader(xc -> new Session(xc.request.session))
//                    ))
//                )
//            );
//      new HttpApi<HttpServletExchange>() {{
//          withInstance(new Instance() {{
//          )
//      }}
//


        var conf = new HttpServletServer(
            service(
                new UrlShortenerApi(ds),
                method(
                    "decode",
                    // http:some.ru/UrlShortenerApi.decode/wqd12
                    // http:some.ru/l/wqd12
                    new PathVariableReader(),
                    new RedirectWriter()
                )
            )
        );
    }
}
