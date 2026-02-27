package http.parser.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.ByteStream;
import static http.Base.SKIP_OWS;
import static http.JumpTables.*;

public class AcceptLanguageParser {

    public sealed interface LanguageRange {
        record Star() implements LanguageRange { }
        record One(String value) implements LanguageRange { }
        record Range(String rangeStart, String rangeEnd) implements LanguageRange { }
    }

    public record LanguageRangeWithWeight (LanguageRange range, Float weight) {}

    public static class AcceptLanguage {
        public final ArrayList<LanguageRangeWithWeight> value;

        public AcceptLanguage(ArrayList<LanguageRangeWithWeight> value) {
            this.value = value;
        }
    }

    public static byte ALPHA_STAR_OPT(ByteStream bs) {
        var b = bs.advance();
        if ((b >= 'A' && b <= 'Z') || (b >= 'a' && b <= 'z') || b == '*') return b;
        else { bs.unadvance(b); return -1; }
    }

    public static LanguageRange LANGUAGE_RANGE(ByteStream bs, Buffer bfr) {
        byte b;
        if ((b = ALPHA_STAR_OPT(bs)) == -1)
            return null;

        if (b == '*')
            return new LanguageRange.Star();
        else {
            bs.unadvance(b);
            bfr.reset();

            TOKEN(bs, bfr, IS_ALPHA_TABLE, -1);

            if (bfr.remains() > 8)
                throw new RuntimeException("Expected 1*8ALPHA, has " + bfr.remains() + " ALPHA");

            var rangeStart = bfr.toStringAndReset();

            if (CHAR_OPT(bs, '-') == -1)
                return new LanguageRange.One(rangeStart);

            if ((b = ALPHA_DIGIT_OPT(bs)) == -1)
                throw new RuntimeException("Expected 1*8alphanum");
            bs.unadvance(b);

            TOKEN(bs, bfr, IS_ALPHA_DIGIT_TABLE, -1);

            if (bfr.remains() > 8)
                throw new RuntimeException("Expected 1*8alphanum, has " + bfr.remains() + " alphanum");

            var rangeEnd = bfr.toStringAndReset();
            return new LanguageRange.Range(rangeStart, rangeEnd);
        }
    }

    //[ ( language-range [ weight ] ) *( OWS "," OWS (language-range [ weight ] ) ) ]
    public static AcceptLanguage ACCEPT_LANGUAGE(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<LanguageRangeWithWeight>();
        bfr.reset();

        while (true) {
            var languageRange = LANGUAGE_RANGE(bs, bfr);
            if (languageRange == null)
                break;

            var weightOpt = WEIGHT_FROM_BS_OPT(bs, bfr);

            value.add(new LanguageRangeWithWeight(languageRange, weightOpt != -1 ? weightOpt : null));

            SKIP_OWS(bs);
            if (CHAR_OPT(bs, ',') != -1) break;
            SKIP_OWS(bs);
        }

        return new AcceptLanguage(value);
    }
}
