package http.middleware;

import java.util.List;

public class BeforeMiddleware implements Middleware {
    @Override public List<Endpoint> process(List<Endpoint> endpoints) {
        return List.of();
    }
}
