package http.header;

import http.HttpParser;
import http.HttpParser.Header;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.Base.*;
import static http.JumpTables.IS_TCHAR_TABLE;
import static http.header.ContentTypeHeader.*;

public class ContentTypeHeader implements HeaderParser<ContentType>, HeaderEncoder<ContentType> {

    public static class ContentType extends Header {
        public final String type;
        public final String subtype;
        public final Parameters value;

        public ContentType(String type, String subtype, Parameters value) {
            this.type = type;
            this.subtype = subtype;
            this.value = value;
        }
    }

    @Override
    public byte[] ENCODE_HEADER(ContentType header) {
        return new byte[0];
    }

    @Override
    public ContentType PARSE_HEADER(ByteStream bs, Buffer bfr) {

        if (!TCHAR_CHECK(bs)) throw new RuntimeException("Expected token");
        TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
        var type = bfr.toStringAndReset();

        CHAR(bs, '/');

        if (!TCHAR_CHECK(bs)) throw new RuntimeException("Expected token");
        TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
        var subtype = bfr.toStringAndReset();

        var parameters = new ArrayList<Parameter>();
        while (true) {
            var nameEndIdx = PARAMETER(bs, bfr);
            if (nameEndIdx == 0) break;
            parameters.add(
                new Parameter(
                    new String(bfr.bytes, 0, nameEndIdx, StandardCharsets.UTF_8),
                    new String(bfr.bytes, nameEndIdx, bfr.remains(), StandardCharsets.UTF_8))
            );
        }

        return new ContentType(type, subtype, new Parameters(parameters));
    }
}
