package http.header.algorithms;

import http.BaseEncoder;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ServerParserEncoder implements ValueListHeaderParser<Product, Server> {

    @Override
    public Server create(ArrayList<Product> valueArray) {
        return null;
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<Product> dest) {
        PRODUCTS(bs, bfr, dest);
    }

    @Override
    public void encode(BaseEncoder.ResponseByteStream rbs, Server header) {
        BaseEncoder.PRODUCTS(rbs, header.value);
    }
}
