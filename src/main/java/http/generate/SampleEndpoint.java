package http.generate;

import http.BaseDecoder.ByteStream;
import http.BaseEncoder.ResponseByteStream;

import java.util.ArrayList;
import java.util.List;

public class SampleEndpoint implements Endpoint {
    public static class Body {}

    public class Module {
        public String hello(Body body) {
            return "Hello World";
        }
    }

    public final Module module = new Module();

    public final static String route = "/hello";
    public final static String method = "GET";

    public final static ArrayList<String> headers =
        new ArrayList<>(List.of("Content-Type", "Authorization", "Accept-Encoding", "Content-Length"));

    public void invoke(ByteStream bs, ResponseByteStream rbs) {
        // parse and init ALL headers

        // call middlewares

        // on demand parse body
        final var body = parseBody(bs);
        // do endpoint
        final var x = module.hello(body);
        // write response
            // start line
            // headers
            // body

        // some other middleware
    }

    public static Body parseBody(ByteStream bs) {
        return null;
    }

}
