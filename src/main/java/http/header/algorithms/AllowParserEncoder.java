package http.header.algorithms;


import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import java.util.ArrayList;

import static http.HttpParser.*;
import static http.BaseDecoder.*;
import static http.header.DTOs.*;

public class AllowParserEncoder implements ValueListHeaderParser<Method, Allow> {
    @Override
    public Allow create(ArrayList<Method> valueArray) {
        return new Allow(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<Method> dest) {
        METHODS(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, Allow header) {
        BaseEncoder.METHODS(rbs, header.value);
    }
}
