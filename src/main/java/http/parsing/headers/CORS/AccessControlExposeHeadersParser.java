package http.parsing.headers.CORS;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;

import java.util.ArrayList;

import static http.dto.Headers.*;
import static http.parsing.BaseDecoder.TOKENS_COMMA_SEPARATED;

public class AccessControlExposeHeadersParser implements ListHeaderParser<String, AccessControlExposeHeaders> {

    @Override
    public AccessControlExposeHeaders create(ArrayList<String> valueArray) {
        return new AccessControlExposeHeaders(valueArray);
    }

    @Override
    public void decode(RequestByteStream bs, Buffer bfr, ArrayList<String> dest) {
        TOKENS_COMMA_SEPARATED(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlExposeHeaders header) {
        BaseEncoder.TOKENS_COMMA_SEPARATED(rbs, header.fieldNames);
    }
}
