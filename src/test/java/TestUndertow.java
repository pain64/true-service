import io.undertow.Undertow;
import io.undertow.util.Headers;
import net.truej.service.header.AcceptHeader;
import net.truej.service.header.Headers2;
import net.truej.service.header.LocationHeader;
import net.truej.service.union.Union2;

import java.lang.management.ManagementFactory;

// class SiteAuthMiddleware {
//     public <T> Union2<T, Sc401> process(
//         HttpSession session, Next1<Auth, T> next
//     ) {
//         var auth = (Auth) session.getAttribute("TRACKER_APP_AUTH");
//         return auth != null
//             ? Union2.of1(next.apply(auth))
//             : Union2.of2(new Sc401());
//     }
// }
//
// class SiteAuthMiddleware {
//     public <T> ByStatusCode1<Sc401, Void, T> process(
//         HttpSession session, Next1<Auth, T> next
//     ) {
//         var auth = (Auth) session.getAttribute("TRACKER_APP_AUTH");
//         return auth == null
//             ? ByStatusCode1.of1(Sc401.of, null)
//             : ByStatusCode1.ofElse(next.apply(auth))
//     }
// }
//
// class BotAuthMiddleware {
//     public <T> Union2<T, Sc401> process(
//         AuthorizationHeader token, Next0<T> next
//     ) {
//         return token.value.equals(botAuthToken)
//             ? Union2.of1(next.apply())
//             : Union2.of2(new Sc401());
//     }
// }

// class ClubAuthMiddleware {
//     public <T> Union2<T, Sc401> process(
//         Auth auth, Long clubId, Next0<T> next
//     ) {
//         return auth.isManagerOfClub(clubId)
//             ? Union2.of1(next.apply())
//             : Union2.of2(new Sc401());
//     }
// }

Union2<String, Integer> get(boolean cond) {
    //return cond ? new Union2.V1<>("") : new Union2.V2<>(32);
    return cond ? Union2.of1("") : Union2.of2(32);
}

void bar() {
    switch (get(true)) {
        // case Union5.V1<String, ?, ?, ?, ?> v -> {}
        // case Union5.V2<?, Integer, ?, ?, ?> v -> {}
        // case Union5.V3<?, ?, Long, ?, ?> v -> {}
        // case Union5.V4<?, ?, ?, Float, ?> v -> {}
        // case Union5.V5<?, ?, ?, ?, Double> v -> {}

        case Union2.V1<String, ?> v -> { }
        case Union2.V2<?, Integer> v -> { }
    }
}

// 1. literal header values ???
// 2. literal status codes
// 3. unions needed only for middleware ???
// 4.

// Entity2 ???
// Record2<
//     LocationHeader,
//     AcceptHeader,
//     StatusCode202,
//     String
// >

void main() {
    new Headers2<>(
        new LocationHeader("https://google.com"),
        new AcceptHeader("application/*")
    );

    IO.println("time: " + ManagementFactory.getRuntimeMXBean().getUptime() + "ms");

    var server = Undertow.builder()
        .addHttpListener(8080, "localhost")
        .setHandler(exchange -> {
            exchange.getResponseHeaders()
                .put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender()
                .send("Hello World");
        }).build();
    server.start();

    IO.println("time: " + ManagementFactory.getRuntimeMXBean().getUptime() + "ms");
}
