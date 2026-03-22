package http.header.algorithms.CORS;

import http.BaseDecoder;
import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import http.HttpParser;
import http.HttpParser.ValueListHeader;
import http.header.DTOs;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.header.DTOs.*;

public class AccessControlAllowMethodsParser implements HttpParser.ValueListHeaderParser<Method, AccessControlAllowMethods> {

    @Override
    public AccessControlAllowMethods create(ArrayList<Method> valueArray) {
        return new AccessControlAllowMethods(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<Method> dest) {
        METHODS(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, AccessControlAllowMethods header) {
        BaseEncoder.METHODS(rbs, header.allowedMethods);
    }
}
