package http.header.algorithms;

import http.BaseEncoder;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class UserAgentParserEncoder implements ValueListHeaderParser<Product, UserAgent> {
    @Override
    public UserAgent create(ArrayList<Product> valueArray) {
        return new UserAgent(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<Product> dest) {
        PRODUCTS(bs, bfr, dest);
    }

    @Override
    public void encode(BaseEncoder.ResponseByteStream rbs, UserAgent header) {
        BaseEncoder.PRODUCTS(rbs, header.value);
    }
}
