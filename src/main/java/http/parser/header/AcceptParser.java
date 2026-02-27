package http.parser.header;

import http.Base.Buffer;
import http.Base.ByteStream;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.Base.*;
import static http.JumpTables.IS_TCHAR_TABLE;
import static http.JumpTables.TCHAR_OPT;

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
            CHAR(bs, '/'); CHAR(bs, '*');
            return new MediaRangeTypes.StarStar();
        } else bs.unadvance(b);

        TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);

        var type = bfr.toStringAndReset();
        CHAR(bs, '/');

        if ((b = bs.advance()) == '*') {
            return new MediaRangeTypes.TokenStar(type);
        } else bs.unadvance(b);

        bfr.reset();
        TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);

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

            if (!OWS_SYMBOL_OWS_SKIP(bs, ',')) break;
        }
        return new Accept(value);
    }
}
