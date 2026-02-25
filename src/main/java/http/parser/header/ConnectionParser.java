package http.parser.header;

import http.Base;

import java.util.ArrayList;

import static http.Base.*;

public class ConnectionParser {
    public static class Connection {
        public final ArrayList<String> value;

        public Connection(ArrayList<String> value) {
            this.value = value;
        }
    }

    public static Connection CONNECTION(ByteStream bs, Buffer bfr) {
        return new Connection(TOKENS_COMMA_SEPARATED(bs, bfr));
    }
}
