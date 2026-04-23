package http.parsing.headers;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;

import java.util.ArrayList;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class ServerParserEncoder implements ListHeaderParser<Product, Server> {

    @Override
    public Server create(ArrayList<Product> valueArray) {
        return null;
    }

    @Override
    public void decode(RequestByteStream bs, Buffer bfr, ArrayList<Product> dest) {
        PRODUCTS(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, Server header) {
        BaseEncoder.PRODUCTS(rbs, header.value);
    }
}
