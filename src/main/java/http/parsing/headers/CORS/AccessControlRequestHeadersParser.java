package http.parsing.headers.CORS;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;

import java.util.ArrayList;

import static http.dto.Headers.*;
import static http.parsing.BaseDecoder.TOKENS_COMMA_SEPARATED;

public class AccessControlRequestHeadersParser implements ListHeaderParser<String, AccessControlRequestHeaders> {
    @Override
    public AccessControlRequestHeaders create(ArrayList<String> valueArray) {
        return new AccessControlRequestHeaders(valueArray);
    }

    @Override
    public void decode(RequestByteStream bs, Buffer bfr, ArrayList<String> dest) {
        TOKENS_COMMA_SEPARATED(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlRequestHeaders header) {
        BaseEncoder.TOKENS_COMMA_SEPARATED(rbs, header.requestHeaders);
    }
}
