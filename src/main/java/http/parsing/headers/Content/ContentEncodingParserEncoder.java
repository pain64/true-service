package http.parsing.headers.Content;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;

import java.util.ArrayList;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class ContentEncodingParserEncoder implements ListHeaderParser<String, ContentEncoding> {
    @Override
    public ContentEncoding create(ArrayList<String> valueArray) {
        return new ContentEncoding(valueArray);
    }

    @Override
    public void decode(RequestByteStream bs, Buffer bfr, ArrayList<String> dest) {
        TOKENS_COMMA_SEPARATED(bs, bfr, dest);
    }

    @Override
    public void encode(ResponseByteStream rbs, ContentEncoding header) {
        BaseEncoder.TOKENS_COMMA_SEPARATED(rbs, header.value);
    }
}
