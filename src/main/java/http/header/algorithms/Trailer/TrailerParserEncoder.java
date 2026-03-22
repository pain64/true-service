package http.header.algorithms.Trailer;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class TrailerParserEncoder implements ValueListHeaderParser<String, Trailer> {
    @Override
    public Trailer create(ArrayList<String> valueArray) {
        return new Trailer(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<String> dest) {
        TOKENS_COMMA_SEPARATED(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, Trailer header) {
        BaseEncoder.TOKENS_COMMA_SEPARATED(rbs, header.value);
    }
}
