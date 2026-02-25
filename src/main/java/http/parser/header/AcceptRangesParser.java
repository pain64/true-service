package http.parser.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.BYTE_OPT;
import static http.Base.SKIP_OWS;

public class AcceptRangesParser {

    public sealed interface Range {
        record None() implements Range { }
        record Bytes() implements Range { }
        record Token(String value) implements Range { }
    }

    public static class AcceptRanges {
        public final ArrayList<Range> value;

        public AcceptRanges(ArrayList<Range> value) {
            this.value = value;
        }
    }

    public static AcceptRanges ACCEPT_RANGES(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<Range>();
        bfr.reset();

        var finded = false;
        byte b;
        while (true) {
            if ((b = TCHAR_OPT(bs)) == -1) {
                if (!finded)
                    throw new RuntimeException("Expected range-unit");
                else break;
            }

            bs.unadvance(b);
            TOKEN_OPT(bs, bfr);
            var token = bfr.toStringAndReset();

            value.add(
                switch (token) {
                    case "none" -> new Range.None();
                    case "bytes" -> new Range.Bytes();
                    default -> new Range.Token(token);
            });
            finded = true;

            SKIP_OWS(bs);
            if (BYTE_OPT(bs, ',') != -1) break;
            SKIP_OWS(bs);
        }

        return new AcceptRanges(value);
    }
}
