package http.parsing.headers.CORS;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;

import java.util.ArrayList;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class AccessControlAllowHeadersParser implements ListHeaderParser<String, AccessControlAllowHeaders> {

    @Override
    public AccessControlAllowHeaders create(ArrayList<String> valueArray) {
        return new AccessControlAllowHeaders(valueArray);
    }

    @Override
    public void decode(RequestByteStream bs, Buffer bfr, ArrayList<String> dest) {
        TOKENS_COMMA_SEPARATED(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlAllowHeaders header) {
        BaseEncoder.TOKENS_COMMA_SEPARATED(rbs, header.allowedHeaders);
    }
}
