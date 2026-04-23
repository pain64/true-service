package http.parsing.headers.CORS;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import java.util.function.Function;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class AccessControlRequestMethodParser implements ValueParser<AccessControlRequestMethod> {
    private final Function<RequestByteStream, Method> methodParser;

    public AccessControlRequestMethodParser(Function<RequestByteStream, Method> methodParser) {
        this.methodParser = methodParser;
    }

    @Override
    public AccessControlRequestMethod decode(RequestByteStream bs, Buffer bfr) {
        return new AccessControlRequestMethod(methodParser.apply(bs));
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlRequestMethod header) {
        BaseEncoder.METHOD(rbs, header.method);
    }
}
