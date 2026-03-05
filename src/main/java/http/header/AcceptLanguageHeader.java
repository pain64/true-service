package http.header;

import http.HttpParser;
import http.HttpParser.Header;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.ByteStream;
import static http.JumpTables.*;

public class AcceptLanguageHeader implements HttpParser.HeaderParser<AcceptLanguageHeader.AcceptLanguage>, HttpParser.HeaderEncoder<AcceptLanguageHeader.AcceptLanguage> {

    public sealed interface LanguageRange {
        record Star() implements LanguageRange { }
        record One(String value) implements LanguageRange { }
        record Range(String rangeStart, String rangeEnd) implements LanguageRange { }
    }

    public record LanguageRangeWithWeight (LanguageRange range, Float weight) {}

    public static class AcceptLanguage extends Header {
        public final ArrayList<LanguageRangeWithWeight> value;

        public AcceptLanguage(ArrayList<LanguageRangeWithWeight> value) {
            this.value = value;
        }
    }

    public static LanguageRange LANGUAGE_RANGE(ByteStream bs, Buffer bfr) {
        if (CHAR_CHECK(bs, '*')) {bs.advance(); return new LanguageRange.Star();}

        if (!ALPHA_CHECK(bs)) return null;

        bfr.reset();
        TOKEN(bs, bfr, IS_ALPHA_TABLE, -1);

        if (bfr.remains() > 8)
            throw new RuntimeException("Expected 1*8ALPHA, has " + bfr.remains() + " ALPHA");

        var rangeStart = bfr.toStringAndReset();

        if (!CHAR_CHECK(bs, '-')) return new LanguageRange.One(rangeStart);
        bs.advance();

        var b = ALPHA_DIGIT(bs);
        bs.unadvance(b);

        TOKEN(bs, bfr, IS_ALPHA_OR_DIGIT_TABLE, -1);

        if (bfr.remains() > 8)
            throw new RuntimeException("Expected 1*8alphanum, has " + bfr.remains() + " alphanum");

        return new LanguageRange.Range(rangeStart, bfr.toStringAndReset());
    }

    @Override
    public AcceptLanguage PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<LanguageRangeWithWeight>();

        LanguageRange languageRange;
        while ((languageRange = LANGUAGE_RANGE(bs, bfr)) != null) {
            var weightOpt = WEIGHT_OPT(bs, bfr);

            value.add(new LanguageRangeWithWeight(languageRange, weightOpt != -1 ? weightOpt : null));
            OWS_SYMBOL_OWS_SKIP(bs, ',');
        }
        return new AcceptLanguage(value);
    }

    @Override
    public byte[] ENCODE_HEADER(AcceptLanguage header) {
        return new byte[0];
    }

}
