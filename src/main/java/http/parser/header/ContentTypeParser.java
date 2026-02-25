package http.parser.header;

import http.Base;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.Base.*;

public class ContentTypeParser {
    public static class ContentType {
        public final String type;
        public final String subtype;
        public final Parameters value;

        public ContentType(String type, String subtype, Parameters value) {
            this.type = type;
            this.subtype = subtype;
            this.value = value;
        }
    }

    public static ContentType CONTENT_TYPE(ByteStream bs, Buffer bfr) {
        bfr.reset();
        TOKEN(bs, bfr);
        var type = bfr.toStringAndReset();
        BYTE(bs, '/');
        TOKEN(bs, bfr);
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
