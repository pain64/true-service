package http.dsl;

public class TrueServiceConfiguration {
    static void main() {
        new ApiConfiguration()
            .method("xxx").end().end();

        new ModuleConfiguration()
            .with(
                new CorsMiddleware(),
                new AuthorizationMiddleware()
            )
            /**/.httpApi(new UrlShortenerApi()).end()
            /**/.httpApi(new UrlShortenerApi())
            /*    */.method("xxx").end().end().end()
            .submodule(...)
    }
}
