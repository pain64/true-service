package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ConnectionParserEncoder implements HeaderParser<Connection>, HeaderEncoder<Connection> {
    @Override
    public Connection PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new Connection(TOKENS_COMMA_SEPARATED(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(Connection header) {
        return new byte[0];
    }
}
