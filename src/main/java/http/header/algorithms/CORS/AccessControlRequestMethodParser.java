package http.header.algorithms.CORS;

import http.BaseDecoder;
import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import http.HttpParser;
import http.HttpParser.HeaderParser;
import http.header.DTOs;

import static http.BaseDecoder.*;
import static http.header.DTOs.*;

public class AccessControlRequestMethodParser implements HeaderParser<AccessControlRequestMethod> {

    @Override
    public AccessControlRequestMethod decode(ByteStream bs, Buffer bfr) {
        return new AccessControlRequestMethod(METHOD(bs, bfr));
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlRequestMethod header) {
        BaseEncoder.METHOD(rbs, header.method);
    }
}
