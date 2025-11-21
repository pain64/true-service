package org.example;

import net.truej.service.servlet.HttpServletServer;
import net.truej.service.xxx.Server;
import net.truej.service.TrueTomcatServer;
import net.truej.service.TsClientGenerator;
import net.truej.service.env.Env;
import net.truej.service.servlet.HttpServletExchange;

import javax.sql.DataSource;

import static net.truej.service.xxx.Server.*;

public class Main {
    record Auth() { }

    record SiteAuthApi(DataSource ds, String a, String b, int c) {}
    record AdminApi(DataSource ds) {}
    record BotApi(Object o) {}
    record SidebarApi() {}
    record TagApi(DataSource ds) {}
    record TaskApi(DataSource ds) {}
    record VolunteerApi(DataSource ds) {}


    public static void main(String[] args) {
        // next(
        //     SiteAuthApi.class,
        //     () -> {
        //         next()
        //     },
        //     ClubApi.class
        // )

        // TrueTomcatServer -> Server -> Services
        var isDevMode = false;
        var ds = (DataSource) null;
        var imagesService = (Object) null;

        // new HttpServletMiddleware(
        //    service(new SiteAuthApi())
        //    middleware(
        //        ctx -> {
        //            var xc = ctx.get("exchange", HttpServletExchange.class);
        //            var auth = f(xc); // 401 otherwise
        //            ctx.next(new Extra("auth", Authentication.class, auth));
        //        },
        //        middleware(
        //            ctx -> ctx.next(
        //                ctx.get("auth", Authentication.class).assertIsAdmin()
        //            ),
        //            service(new AdminApi(...))
        //        ),
        //        service(new SidebarApi(...)),
        //        service(new TagApi(...)),
        //        service(new TaskApi(...)),
        //        service(new VolunteerApi(...))
        //    )
        // )

        var config = new HttpServletServer(
            service(
                new SiteAuthApi(
                    ds, null, null, 1
//                    Env.string("BOT_URI"),
//                    Env.string("BOT_BACKEND_SECRET"),
//                    Env.integer("CODE_LIFETIME_SECONDS")
                )
            ),
            service(
                new AdminApi(ds),
                interceptor((xc, invocation) -> {
                    /* require admin authentication or 401 */
                })
            ),
            realm(
                interceptor((xc, invocation) -> {
                    /* require manager or admin authentication or 401 */
                }),
                context(
                    Auth.class, xc -> {
                        /* provide authentication as method parameter */
                        return new Auth();
                    }
                ),
                service(new SidebarApi()),
                service(new TagApi(ds)),
                service(new TaskApi(ds)),
                service(new VolunteerApi(ds))
            ),
            service(
                new BotApi(imagesService),
                interceptor((xc, invocation) -> {
                    /* require bot authentication or 401 */
                })
            )
        );

        if (isDevMode)
            TsClientGenerator.generate(config);

        TrueTomcatServer.serve(9966, config);
    }
}