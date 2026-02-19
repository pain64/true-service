package org.example.shortener;

import lombok.Data;
import net.truej.service.Exchange;
import net.truej.service.servlet.HttpServletExchange;
import net.truej.service.servlet.PathVariableReader;
import net.truej.service.servlet.RedirectWriter;
import org.example.Main;
import org.example.Main.*;
import org.example.shortener.SuperDsl3.Security.AuthSetter;

import javax.sql.DataSource;
import java.util.function.Function;

public class SuperDsl3 {
    private static final String AUTH_ATTR = "TRACKER_APP_AUTH";

    public static class Security {
        public enum Role {ADMIN, MANAGER}

        @Data public static class Auth {
            public final long userId;
            public final Role role;

            public void assertIsAdmin() { }
            public void assertIsManager() { }
        }

        public interface AuthSetter {
            void set(Auth auth);
        }
    }

    static InstanceConfig I = new InstanceConfig();
    static MethodConfig M = new MethodConfig();
    static RealmConfig R = new RealmConfig();

    record SiteAuthApi() { }
    record HeaderReader(String headerName) { }
    record ExchangeReader(Function<HttpServletExchange, ?> f) { }


    public static class MethodConfig {
        public MethodConfig useReader(Object r) { return this; }
        public MethodConfig useReader(Class<?> forType, Object r) { return this; }
        public MethodConfig useWriter(Object w) { return this; }
        public MethodConfig useWriter(Class<?> forType, Object w) { return this; }
    }

    public static class InstanceConfig {
        public InstanceConfig forMethod(String methodName, MethodConfig next) { return this; }
        public InstanceConfig useReader(String parameterName, Object r) { return this; }
        public InstanceConfig useReader(Class<?> forType, Object r) { return this; }
        public InstanceConfig useWriter(Class<?> forType, Object w) { return this; }
    }

    public static class RealmConfig {
        public RealmConfig withInstance(Object instance, InstanceConfig next) { return this; }
        public RealmConfig withInstance(Object instance) { return this; }
        public RealmConfig useReader(Object r) { return this; }
        public RealmConfig useReader(Class<?> forType, Object r) { return this; }
        public RealmConfig useWriter(Object w) { return this; }
        public RealmConfig useWriter(Class<?> forType, Object w) { return this; }
    }

    public static class HttpApi<T extends Exchange> {
        public HttpApi<T> withInstance(Object instance, InstanceConfig next) { return this; }
        public HttpApi<T> withInstance(Object instance) { return this; }
        public HttpApi<T> inRealm(RealmConfig next) { return this; }
    }

    public static class Env {
        public static String getString(String name) { return ""; }
        public static int getInt(String name) { return 1; }
    }

    void main() {
        new HttpApi<HttpServletExchange>()
            .withInstance(
                new UrlShortenerApi(null), I
                    .forMethod(
                        "decode", M
                            //.httpMethodIs(GET)
                            //.httpPathIs("/l")
                            .useReader(new PathVariableReader())
                            .useWriter(new RedirectWriter())
                    )
            );

        var ds = (DataSource) null;
        var imagesService = (Object) null;

        //
        // class ManagerAuthenticationDecorator {
        //     Object decorate(Session session, Next next) {
        //         if (!session.isManager()) throw new AuthException();
        //         next.apply()
        //     }
        // }

        // PerformMethodInstrumentation
        //     .withExtraParameter(Session.class);
        //     .
        //     .forClasses(TagApi.class, VolunteerApi.class, TaskApi.class)
        // Теперь ты можешь использовать классы TagApiIns, VolunteerApiIns, TaskApiIns
        // в которых каждый public метод имеет дополнительный параметр Session session
        //     и в начале каждого метода вызывается session.assertIsManage


        // Принципиальная проблема - константная семантика - GET/ Post, StatusCode, Path, Const headers (Accept)

// HttpSession будет дано при привязке Tomcat Http Servlet API???
        // class HttpServletExchange {
        //     HttpSession provideHttpSession() {}
        // }

//        new TrueWebApi()
//            .webSocket(handler)
//            .serverSentEvents(handler)
//            .http(new FileDownloadApi("/tmp/files"))
//            .http(
//                new SiteAuthApi(
//                    ds, Env.getString("BOT_URI"),
//                    Env.getString("BOT_BACKEND_SECRET"),
//                    Env.getInt("CODE_LIFETIME_SECONDS")
//                )
//            )
//            .with(
//                new SiteAuthMiddleware(), H
//                    .http(new AdminApi(ds))
//                    .http(new SidebarApi())
//                    .http(new TagApi(ds))
//                    .http(new TaskApi(ds))
//                    .http(new VolunteerApi(ds))
//            )
//            .with(
//                new BotAuthMiddleware(
//                    Env.getString("BOT_TOKEN")
//                ), H
//                    .http(new BotApi(imagesService))
//            );


        // ContextReader
        new HttpApi<HttpServletExchange>()
            .withInstance(
                new SiteAuthApi(), I
                    .useReader(
                        AuthSetter.class, new ExchangeReader(xc ->
                            (AuthSetter) auth ->
                                xc.request
                                    .getSession(true)
                                    .setAttribute(AUTH_ATTR, auth)
                        )
                    )
            )
            .inRealm(R
                .useReader(
                    Auth.class, new ExchangeReader(xc -> {
                        var auth = (Auth) xc.request
                            .getSession().getAttribute(AUTH_ATTR);
                        if (auth == null) throw new RuntimeException("oops");
                        return auth;
                    })
                )
                .withInstance(
                    new Main.SiteAuthApi(
                        ds, Env.getString("BOT_URI"),
                        Env.getString("BOT_BACKEND_SECRET"),
                        Env.getInt("CODE_LIFETIME_SECONDS")
                    )
                )
                .withInstance(new AdminApi(ds))
                .withInstance(new SidebarApi())
                .withInstance(new TagApi(ds))
                .withInstance(new TaskApi(ds))
                .withInstance(new VolunteerApi(ds))
            )
            .withInstance(
                new BotApi(imagesService), I.useReader(
                    // FIXME ???
                    "botToken", new HeaderReader("Authorization")
                )
            );
    }
}
