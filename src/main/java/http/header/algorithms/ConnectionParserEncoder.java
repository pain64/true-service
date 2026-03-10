package http.header.algorithms;

import java.util.ArrayList;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ConnectionParserEncoder implements HeaderParserMultiline<ConnectionOption>, HeaderEncoder<Connection> {
    @Override
    public void PARSE_HEADER(ByteStream bs, Buffer bfr, ArrayList<ConnectionOption> toAdd) {
        toAdd.addAll(TOKENS_COMMA_SEPARATED(bs, bfr));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, Connection header) {
        return new byte[0];
    }

}
