package http.middleware;

import java.util.List;

public class AuthorizationMiddleware implements MiddlewareConfig<BeforeMiddleware> {

    public List<Endpoint> process(List<Endpoint> endpoints) {
        // JCExpression config

        // middleware runtime instance ???
        // 1. BeforeMiddleware
        // 2. AfterMiddleware
        // 3. CorsMiddleware
        // 4. ContentEncodingMiddleware
        //
        // AST vs pure code generation
        // try {
        //
        // }
        //
        // class MyAuthorization implements BeforeMiddleware {
        //     static final BeforeMiddlewareDef DEF = new BeforeMiddlewareDef()
        //         .parameterFromCookie("jSessionId", "JSESSIONID")
        //         .returnContext("mAuthentication")
        //         .<AuthException, String> exceptionToBody(
        //             400, "text/plain", e -> e.getMessage
        //         )
        //
        //     /* @Override */ Auth before(long jSessionId) {
        //
        //     }
        // }
        return endpoints.stream()
            // TrueServiceDef
            //     .withAdd
            .map(endpoint -> {
                // endpoint is DATA
                // endpoint
                //     .<String> withAddParameterHeader("authHeader", "Authorization")
                //     .<Authentication> withAddContext("mAuthentication")
                //         .fromStaticMethodInvocation(MyMiddleware::doAuth)
                //         .<AuthException> exceptionToReturnVariant(
                //             400, "text/plain", ex -> ex.getMessage
                //         )
                // endpoint.withReturnVariant(400,
                return endpoint;
            })
            .toList();
        // try {
        //     var , context(mAuth), " = io.xxx.Authentication.doAuth()
        // }
    }
}
