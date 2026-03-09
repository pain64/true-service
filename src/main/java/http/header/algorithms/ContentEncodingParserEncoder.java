package http.header.algorithms;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ContentEncodingParserEncoder implements HeaderParser<ContentEncoding>, HeaderEncoder<ContentEncoding> {
    @Override
    public ContentEncoding PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new ContentEncoding(TOKENS_COMMA_SEPARATED(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(ContentEncoding header) {
        return new byte[0];
    }

}
