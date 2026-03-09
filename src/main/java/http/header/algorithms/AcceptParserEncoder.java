package http.header.algorithms;

import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.Base.*;
import static http.header.DTOs.*;

public class AcceptParserEncoder implements HeaderParser<Accept>, HeaderEncoder<Accept> {
    public static MediaRangeType MEDIA_RANGE_TYPE(ByteStream bs, Buffer bfr) {
        if (IS_CHAR(bs, '*')) {
            CHAR(bs, '*'); CHAR(bs, '/'); CHAR(bs, '*');
            return new MediaRangeType.StarStar();
        }

        TOKEN_TCHAR(bs, bfr);
        var type = bfr.toStringAndReset();
        CHAR(bs, '/');

        if (IS_CHAR(bs, '*')) {
            return new MediaRangeType.TokenStar(type);
        }

        TOKEN_TCHAR(bs, bfr);
        if (bfr.remains() == 0)
            throw new RuntimeException("Expected token");

        return new MediaRangeType.TokenToken(type, bfr.toStringAndReset());
    }

    @Override
    public Accept PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<MediaRange>();

        if (!IS_TCHAR(bs)) return new Accept(value);

        do {
            var mediaRangeType = MEDIA_RANGE_TYPE(bs, bfr);

            Float weight = null;
            var parameters = new ArrayList<Parameter>();

            while (OWS_SYMBOL_OWS_SKIP(bs, ';')) {
                bfr.reset();
                if (IS_CHAR(bs, 'q')) weight = WEIGHT(bs, bfr);
                else {
                    var paramNameLength = PARAMETER(bs, bfr);
                    var paramName = new String(bfr.bytes, 0, paramNameLength, StandardCharsets.UTF_8);
                    parameters.add(new Parameter(paramName, new String(bfr.bytes, paramNameLength, bfr.remains(), StandardCharsets.UTF_8)));
                }
            }

            value.add(new MediaRange(mediaRangeType, parameters, weight));
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));

        return new Accept(value);
    }

    @Override
    public byte[] ENCODE_HEADER(Accept header) {
        return new byte[0];
    }
}
