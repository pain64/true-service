package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ServerParserEncoder implements HeaderParser<Server>, HeaderEncoder<Server> {
    @Override
    public Server PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new Server(PRODUCTS(bs, bfr));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, Server header) {
        return new byte[0];
    }
}
