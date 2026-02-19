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
    public record Auth() { }

    public record SiteAuthApi(DataSource ds, String a, String b, int c) {}
    public record AdminApi(DataSource ds) {}
    public record BotApi(Object o) {}
    public record SidebarApi() {}
    public record TagApi(DataSource ds) {}
    public record TaskApi(DataSource ds) {}
    public record VolunteerApi(DataSource ds) {}


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

// m1 m2 m3 m4 f
// middleware is a compile-time code factory function
//     all config + middleware config => java method code
// core: f(context, args) -> r
//       realm + middleware

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

//        var config = new HttpServletServer(
//            service(
//                new SiteAuthApi(
//                    ds, null, null, 1
//                    Env.string("BOT_URI"),
//                    Env.string("BOT_BACKEND_SECRET"),
//                    Env.integer("CODE_LIFETIME_SECONDS")
//                ),
//                method("login", sessionAttributeResultWriter(
//                    "authentication"
//                ))
//            ),
//            service(
//                new AdminApi(ds),
//                parameter(
//                    "adminAuthentication", AdminAuth.class,
//                    sessionParameterReader(
//                        "authentication", v -> { assert v instanceof AdminAuth }
//                    )
//                )
//            ),
//            realm(
//                parameter(
//                    "clubId", long.class,
//                     handler(
//                                 
//                     )
//                     TODO: need authentication here !!!
//                ),
//                parameter(
//                    "authentication", Auth.class,
//                    sessionParameterReader("authenticaton")
//                ),
//                service(new SidebarApi()),
//                service(new TagApi(ds)),
//                service(new TaskApi(ds)),
//                service(new VolunteerApi(ds))
//            ),
//            service(
//                parameter(
//                    "botAuthentication", Void.class,
//                    headerParameterReader(
//                        "X-BOT-AUTH", hv -> { ... }
//                    )
//                ),
//                new BotApi(imagesService),
//            )
//        );

//        var config = new HttpServletServer(
//            services(
//                reader(
//                    Session.class, new HttpSessionReader(hs -> new Session(hs))
//                ),
//                service(
//                    new SiteAuthApi(
//                        ds, null, null, 1
//                        Env.string("BOT_URI"),
//                        Env.string("BOT_BACKEND_SECRET"),
//                        Env.integer("CODE_LIFETIME_SECONDS")
//                    )
//                ),
//                service(new AdminApi(ds)),
//                service(new SidebarApi()),
//                service(new TagApi(ds)),
//                service(new TaskApi(ds)),
//                service(new VolunteerApi(ds))
//            ),
//            service(
//                reader(String.class, "token", xc -> {
//                    var token = xc.request.getHeader("X-BOT-TOKEN");
//                    if (token == null) throw new ParameterUnmatchedException();
//                    else return token;
//                }),
//                new BotApi(imagesService)
//            )
//        );

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
                // context(Auth.class, "auth", xc -> { })
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