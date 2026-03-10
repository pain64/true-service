package http.header.algorithms;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ContentTypeParserEncoder implements HeaderParser<ContentType>, HeaderEncoder<ContentType> {
    @Override
    public ContentType PARSE_HEADER(ByteStream bs, Buffer bfr) {

        TOKEN_TCHAR(bs, bfr);
        var type = bfr.toStringAndReset();

        CHAR(bs, '/');

        TOKEN_TCHAR(bs, bfr);
        var subtype = bfr.toStringAndReset();

        var parameters = new ArrayList<Parameter>();
        while (OWS_DELIMITER_OWS_SKIP(bs, ';')) {
            var paramNameLength = PARAMETER(bs, bfr);
            parameters.add(
                new Parameter(
                    new String(bfr.bytes, 0, paramNameLength, StandardCharsets.UTF_8),
                    new String(bfr.bytes, paramNameLength, bfr.remains(), StandardCharsets.UTF_8))
            );
        }

        return new ContentType(type, subtype, parameters);
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, ContentType header) {
        return new byte[0];
    }
}
