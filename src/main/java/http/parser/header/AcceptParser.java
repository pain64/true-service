package http.parser.header;

import http.Base.Buffer;
import http.Base.ByteStream;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.Base.*;

public class AcceptParser {
    //Accept = [ ( media-range [ weight ] ) *( OWS "," OWS ( media-range [weight ] ) ) ]

    public sealed interface MediaRangeTypes {
        record StarStar() implements MediaRangeTypes { }
        record TokenStar(String type) implements MediaRangeTypes { }
        record TokenToken(String type, String subtype) implements MediaRangeTypes { }
    }

    public record MediaRange (MediaRangeTypes mediaRange, Parameters parameters, Float weight) {}

    public static class Accept {
        public final ArrayList<MediaRange> value;

        public Accept(ArrayList<MediaRange> value) {
            this.value = value;
        }
    }

    public static MediaRangeTypes MEDIA_RANGE(ByteStream bs, Buffer bfr) {
        byte b;
        if ((b = TCHAR_OPT(bs)) == -1)
            return null;

        if (b == '*') {
            BYTE(bs, '/'); BYTE(bs, '*');
            return new MediaRangeTypes.StarStar();
        } else bs.unadvance(b);

        TOKEN_OPT(bs, bfr);

        var type = bfr.toStringAndReset();
        BYTE(bs, '/');

        if ((b = bs.advance()) == '*') {
            return new MediaRangeTypes.TokenStar(type);
        } else bs.unadvance(b);

        bfr.reset();
        TOKEN_OPT(bs, bfr);

        if (bfr.remains() == 0)
            throw new RuntimeException("Expected token");

        return new MediaRangeTypes.TokenToken(type, bfr.toStringAndReset());
    }

    public static Accept ACCEPT(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<MediaRange>();
        var parameters = new ArrayList<Parameter>();
        bfr.reset();

        while (true) {
            var weightOpt = (float) -1;
            var mediaRange = MEDIA_RANGE(bs, bfr);
            if (mediaRange == null) break;

            while (true) {
                bfr.reset();
                var parameterNameLength = PARAMETER(bs, bfr);
                if (parameterNameLength == 0) break;
                var parameterName = new String(bfr.bytes, 0, parameterNameLength, StandardCharsets.UTF_8);

                if (!"q".equals(parameterName)) {
                    parameters.add(new Parameter(parameterName, new String(bfr.bytes, parameterNameLength, bfr.remains(), StandardCharsets.UTF_8)));
                } else {
                    weightOpt = WEIGHT_FROM_BFR(bfr, parameterNameLength);
                }
            }

            value.add(
                new MediaRange(
                    mediaRange,
                    new Parameters(parameters),
                    weightOpt != -1 ? weightOpt : null)
            );

            if (!OWS_COMMA_OWS(bs)) break;
        }
        return new Accept(value);
    }
}
