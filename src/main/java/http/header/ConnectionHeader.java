package http.header;

import http.HttpParser;
import http.HttpParser.Header;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;

import java.util.ArrayList;

import static http.Base.*;
import static http.header.ConnectionHeader.*;

public class ConnectionHeader implements HeaderParser<Connection>, HeaderEncoder<Connection> {
    public static class Connection extends Header {
        public final ArrayList<String> value;

        public Connection(ArrayList<String> value) {
            this.value = value;
        }
    }

    @Override
    public Connection PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new Connection(TOKENS_COMMA_SEPARATED(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(Connection header) {
        return new byte[0];
    }
}
