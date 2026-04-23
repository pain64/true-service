package http.parsing.headers.CORS;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;

import java.util.ArrayList;
import java.util.function.Function;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class AccessControlAllowMethodsParser implements ListHeaderParser<Method, AccessControlAllowMethods> {
    private final Function<RequestByteStream, Method> methodParser;

    public AccessControlAllowMethodsParser(Function<RequestByteStream, Method> methodParser) {
        this.methodParser = methodParser;
    }

    @Override
    public AccessControlAllowMethods create(ArrayList<Method> valueArray) {
        return new AccessControlAllowMethods(valueArray);
    }

    @Override
    public void decode(RequestByteStream rbs, Buffer bfr, ArrayList<Method> dest) {
        if (!IS_TCHAR(rbs)) return;

        do { dest.add(methodParser.apply(rbs)); } while (OWS_DELIMITER_OWS_SKIP(rbs, ','));
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlAllowMethods header) {
        BaseEncoder.METHODS(rbs, header.allowedMethods);
    }
}
