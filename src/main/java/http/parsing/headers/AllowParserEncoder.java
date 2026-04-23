package http.parsing.headers;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;
import io.netty.handler.codec.spdy.SpdyWindowUpdateFrame;

import java.util.ArrayList;
import java.util.function.Function;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class AllowParserEncoder implements ListHeaderParser<Method, Allow> {
    private final Function<RequestByteStream, Method> methodParser;

    public AllowParserEncoder(Function<RequestByteStream, Method> methodParser) {
        this.methodParser = methodParser;
    }

    @Override
    public Allow create(ArrayList<Method> valueArray) {
        return new Allow(valueArray);
    }

    @Override
    public void decode(RequestByteStream rbs, Buffer bfr, ArrayList<Method> dest) {
        if (!IS_TCHAR(rbs)) return;

        do { dest.add(methodParser.apply(rbs)); } while (OWS_DELIMITER_OWS_SKIP(rbs, ','));

    }

    @Override
    public void encode(ResponseByteStream rbs, Allow header) {
        BaseEncoder.METHODS(rbs, header.value);
    }
}
