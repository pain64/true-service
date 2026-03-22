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

public class AccessControlAllowHeadersParser implements ValueListHeaderParser<String, AccessControlAllowHeaders> {

    @Override
    public AccessControlAllowHeaders create(ArrayList<String> valueArray) {
        return new AccessControlAllowHeaders(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<String> dest) {
        TOKENS_COMMA_SEPARATED(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlAllowHeaders header) {
        BaseEncoder.TOKENS_COMMA_SEPARATED(rbs, header.allowedHeaders);
    }
}
