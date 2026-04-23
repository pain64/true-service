package http.parsing.headers.Accept;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class AcceptParserEncoder implements ListHeaderParser<MediaRange, Accept> {
    public static MediaRangeType MEDIA_RANGE_TYPE(RequestByteStream bs, Buffer bfr) {
        if (IS_CHAR(bs, '*')) {
            bs.advance(); CHAR(bs, '/'); CHAR(bs, '*');
            return new MediaRangeType.StarStar();
        }

        TOKEN_TCHAR(bs, bfr);
        var type = bfr.toStringAndReset();
        CHAR(bs, '/');

        if (IS_CHAR(bs, '*')) {
            bs.advance(); return new MediaRangeType.TokenStar(type);
        }

        TOKEN_TCHAR(bs, bfr);

        return new MediaRangeType.TokenToken(type, bfr.toStringAndReset());
    }

    @Override
    public Accept create(ArrayList<MediaRange> valueArray) {
        return new Accept(valueArray);
    }

    @Override
    public void decode(RequestByteStream bs, Buffer bfr, ArrayList<MediaRange> dest) {
        if (!IS_TCHAR(bs)) return;

        do {
            var mediaRangeType = MEDIA_RANGE_TYPE(bs, bfr);

            var weight = (Float) null;
            var parameters = new ArrayList<Parameter>();

            while (OWS_DELIMITER_OWS_SKIP(bs, ';')) {
                bfr.reset();
                if (IS_CHAR(bs, 'q')) weight = WEIGHT(bs, bfr);
                else {
                    var paramNameLength = PARAMETER(bs, bfr);
                    var paramName = new String(bfr.bytes, 0, paramNameLength, StandardCharsets.UTF_8);
                    parameters.add(new Parameter(paramName, new String(bfr.bytes, paramNameLength, bfr.remains()-paramNameLength, StandardCharsets.UTF_8)));
                }
            }

            dest.add(new MediaRange(mediaRangeType, parameters, weight));
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
    }

    @Override
    public void encode(ResponseByteStream rbs, Accept header) {
        for (var i = 0; i < header.value.size(); i++) {
            var mediaRange = header.value.get(i).mediaRange;
            var parameters = header.value.get(i).parameters;
            var weight = header.value.get(i).weight;

            if (mediaRange instanceof MediaRangeType.StarStar) rbs.push("*/*");
            else if (mediaRange instanceof MediaRangeType.TokenStar) {
                rbs.push(((MediaRangeType.TokenStar) mediaRange).type);
                rbs.push("/*");
            }
            else {
                rbs.push(((MediaRangeType.TokenToken) mediaRange).type);
                rbs.push('/');
                rbs.push(((MediaRangeType.TokenToken) mediaRange).subtype);
            }

            BaseEncoder.PARAMETERS(rbs, parameters);
            BaseEncoder.WEIGHT(rbs, weight);

            if (header.value.size()-1 != i) rbs.push(',');
        }
    }
}
