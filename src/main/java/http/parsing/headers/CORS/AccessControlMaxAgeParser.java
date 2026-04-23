package http.parsing.headers.CORS;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.dto.Headers.*;
import static http.parsing.BaseDecoder.UNSIGNED_LONG;

public class AccessControlMaxAgeParser implements ValueParser<AccessControlMaxAge> {

    @Override
    public AccessControlMaxAge decode(RequestByteStream bs, Buffer bfr) {
        return new AccessControlMaxAge(UNSIGNED_LONG(bs));
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlMaxAge header) {
        BaseEncoder.NUMBER(rbs, header.value);
    }
}
