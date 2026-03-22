package http.header.algorithms;

import http.BaseEncoder;
import http.HttpParser;
import net.truej.service.C;

import java.util.ArrayList;

import static http.HttpParser.*;
import static http.BaseDecoder.*;
import static http.header.DTOs.*;

public class ConnectionParserEncoder implements ValueListHeaderParser<String, Connection> {
    @Override
    public Connection create(ArrayList<String> valueArray) {
        return new Connection(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<String> dest) {
        TOKENS_COMMA_SEPARATED(bs, bfr, dest);
    }

    @Override
    public void encode(BaseEncoder.ResponseByteStream rbs, Connection header) {
        BaseEncoder.TOKENS_COMMA_SEPARATED(rbs, header.value);
    }
}
