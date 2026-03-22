package http.header.algorithms.Content;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ContentTypeParserEncoder implements HeaderParser<ContentType> {
    @Override
    public ContentType decode(ByteStream bs, Buffer bfr) {
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
                    new String(bfr.bytes, paramNameLength, bfr.remains()-paramNameLength, StandardCharsets.UTF_8))
            );
        }

        return new ContentType(type, subtype, parameters);
    }

    @Override
    public void encode(ResponseByteStream rbs, ContentType header) {
        rbs.push(header.type);
        rbs.push('/');
        rbs.push(header.subtype);
        BaseEncoder.PARAMETERS(rbs, header.value);
    }
}
