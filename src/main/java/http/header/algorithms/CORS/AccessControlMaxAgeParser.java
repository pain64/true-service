package http.header.algorithms.CORS;

import http.BaseDecoder;
import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import http.HttpParser;
import http.HttpParser.HeaderParser;
import http.header.DTOs;

import static http.BaseDecoder.*;
import static http.header.DTOs.*;

public class AccessControlMaxAgeParser implements HeaderParser<AccessControlMaxAge> {

    @Override
    public AccessControlMaxAge decode(ByteStream bs, Buffer bfr) {
        return new AccessControlMaxAge(ONE_OR_MORE_DIGIT_NUMBER(bs));
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlMaxAge header) {
        BaseEncoder.NUMBER(rbs, header.value);
    }
}
