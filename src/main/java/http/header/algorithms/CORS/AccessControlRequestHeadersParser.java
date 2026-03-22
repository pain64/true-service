package http.header.algorithms.CORS;

import http.BaseDecoder;
import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import http.HttpParser;
import http.HttpParser.ValueListHeaderParser;
import http.header.DTOs;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.header.DTOs.*;

public class AccessControlRequestHeadersParser implements ValueListHeaderParser<String, AccessControlRequestHeaders> {
    @Override
    public AccessControlRequestHeaders create(ArrayList<String> valueArray) {
        return new AccessControlRequestHeaders(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<String> dest) {
        TOKENS_COMMA_SEPARATED(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlRequestHeaders header) {
        BaseEncoder.TOKENS_COMMA_SEPARATED(rbs, header.requestHeaders);
    }
}
