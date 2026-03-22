package http.header.algorithms.Content;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ContentEncodingParserEncoder implements ValueListHeaderParser<String, ContentEncoding> {
    @Override
    public ContentEncoding create(ArrayList<String> valueArray) {
        return new ContentEncoding(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<String> dest) {
        TOKENS_COMMA_SEPARATED(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, ContentEncoding header) {
        BaseEncoder.TOKENS_COMMA_SEPARATED(rbs, header.value);
    }
}
