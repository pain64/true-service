package http.parsing.headers.CORS;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class AccessControlAllowCredentialsParser implements ValueParser<AccessControlAllowCredentials> {

    @Override
    public AccessControlAllowCredentials decode(RequestByteStream bs, Buffer bfr) {
        CHAR(bs, 't'); CHAR(bs, 'r'); CHAR(bs, 'u'); CHAR(bs, 'e');
        return new AccessControlAllowCredentials();
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlAllowCredentials header) {
        rbs.push("true");
    }
}
