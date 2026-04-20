package http.middleware;

import java.util.List;

public interface Middleware {
    List<Endpoint> process(List<Endpoint> endpoints);
}
