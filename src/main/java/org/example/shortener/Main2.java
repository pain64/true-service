package org.example.shortener;

import net.truej.service.servlet.*;
import net.truej.service.xxx.Server;
import net.truej.service.TrueTomcatServer;

import javax.sql.DataSource;

import java.lang.management.ManagementFactory;

import static net.truej.service.xxx.Server.*;

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

        // http vs json ???
        // httpServletMiddleware(
        //     service(
        //         new UrlShortenerApi(ds),
        //         middleware(
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


        var conf = new HttpServletServer(
            service(
                new UrlShortenerApi(ds),
                method(
                    "decode",
                    // http:some.ru/UrlShortenerApi.decode/wqd12
                    // http:some.ru/l/wqd12
                    new PathVariableParametersReader(),
                    new RedirectResultWriter()
                )
            )
        );
    }
}
