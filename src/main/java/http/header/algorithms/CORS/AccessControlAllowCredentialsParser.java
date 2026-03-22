package http.header.algorithms.CORS;

import http.BaseDecoder;
import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import http.HttpParser;
import http.HttpParser.HeaderParser;
import http.header.DTOs;

import static http.BaseDecoder.*;
import static http.header.DTOs.*;

public class AccessControlAllowCredentialsParser implements HeaderParser<AccessControlAllowCredentials> {

    @Override
    public AccessControlAllowCredentials decode(ByteStream bs, Buffer bfr) {
        CHAR(bs, 't'); CHAR(bs, 'r'); CHAR(bs, 'u'); CHAR(bs, 'e');
        return new AccessControlAllowCredentials();
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlAllowCredentials header) {
        rbs.push("true");
    }
}
