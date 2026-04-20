package http.dsl;

public class UrlShortenerApi {
    static {
        new ApiConfiguration()
            .method("decode")
            /**/.httpMethod(GET)
            /**/.parameterFromPath("id")
            /**/.resultScalar()
            /*    */.toHeader("Location");
    }

    public String decode(String id) {
        return id; // TODO
    }
}
