package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ServerParserEncoder implements HeaderParser<Server>, HeaderEncoder<Server> {
    @Override
    public Server PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new Server(PRODUCTS(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(Server header) {
        return new byte[0];
    }
}
