package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ContentEncodingParserEncoder implements HeaderParser<ContentEncoding>, HeaderEncoder<ContentEncoding> {
    @Override
    public ContentEncoding PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new ContentEncoding(TOKENS_COMMA_SEPARATED(bs, bfr));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, ContentEncoding header) {
        return new byte[0];
    }

}
