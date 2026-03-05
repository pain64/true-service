package http.header;

import http.Base.Buffer;
import http.Base.ByteStream;
import http.HttpParser.Header;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.Base.*;
import static http.JumpTables.IS_TCHAR_TABLE;
import static http.header.AcceptHeader.*;

public class AcceptHeader implements HeaderParser<Accept>, HeaderEncoder<Accept> {

    public sealed interface MediaRangeType {
        record StarStar() implements MediaRangeType { }
        record TokenStar(String type) implements MediaRangeType { }
        record TokenToken(String type, String subtype) implements MediaRangeType { }
    }

    public record MediaRange (MediaRangeType mediaRange, Parameters parameters, Float weight) {}

    public static class Accept extends Header {
        public final ArrayList<MediaRange> value;

        public Accept(ArrayList<MediaRange> value) {
            this.value = value;
        }
    }

    public static MediaRangeType MEDIA_RANGE_TYPE(ByteStream bs, Buffer bfr) {
        if (!TCHAR_CHECK(bs)) return null;

        if (CHAR_CHECK(bs, '*')) {
            CHAR(bs, '*'); CHAR(bs, '/'); CHAR(bs, '*');
            return new MediaRangeType.StarStar();
        }

        TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
        var type = bfr.toStringAndReset();
        CHAR(bs, '/');

        if (CHAR_CHECK(bs, '*')) {
            return new MediaRangeType.TokenStar(type);
        }

        TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
        if (bfr.remains() == 0)
            throw new RuntimeException("Expected token");

        return new MediaRangeType.TokenToken(type, bfr.toStringAndReset());
    }

    @Override
    public Accept PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<MediaRange>();
        var parameters = new ArrayList<Parameter>();

        MediaRangeType mediaRangeType;
        while ((mediaRangeType = MEDIA_RANGE_TYPE(bs, bfr)) != null) {
            var weightOpt = (float) -1;

            while (true) {
                bfr.reset();
                weightOpt = WEIGHT_OPT(bs, bfr);

                if (weightOpt != -1) break;

                var parameterNameLength = PARAMETER(bs, bfr);
                if (parameterNameLength == 0) break;
                var parameterName = new String(bfr.bytes, 0, parameterNameLength, StandardCharsets.UTF_8);
                parameters.add(new Parameter(parameterName, new String(bfr.bytes, parameterNameLength, bfr.remains(), StandardCharsets.UTF_8)));
            }

            value.add(new MediaRange(mediaRangeType, new Parameters(parameters), weightOpt != -1 ? weightOpt : null));

            OWS_SYMBOL_OWS_SKIP(bs, ',');
        }
        return new Accept(value);
    }

    @Override
    public byte[] ENCODE_HEADER(Accept header) {
        return new byte[0];
    }
}
