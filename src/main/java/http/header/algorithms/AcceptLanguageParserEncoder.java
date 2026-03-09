package http.header.algorithms;

import http.HttpParser.*;

import java.util.ArrayList;

import static http.Base.*;
import static http.header.DTOs.*;

public class AcceptLanguageParserEncoder implements HeaderParser<AcceptLanguage>, HeaderEncoder<AcceptLanguage>  {

    public static LanguageRange LANGUAGE_RANGE(ByteStream bs, Buffer bfr) {
        if (IS_CHAR(bs, '*')) {bs.advance(); return new LanguageRange.Star();}

        TOKEN_ALPHA(bs, bfr);

        if (bfr.remains() > 8)
            throw new RuntimeException("Expected 1*8ALPHA, has " + bfr.remains() + " ALPHA");

        var rangeStart = bfr.toStringAndReset();

        if (!IS_CHAR(bs, '-')) return new LanguageRange.One(rangeStart);
        bs.advance();

        var b = ALPHA_DIGIT(bs);
        bs.unadvance(b);

        TOKEN_ALPHA_OR_DIGIT(bs, bfr);

        if (bfr.remains() > 8)
            throw new RuntimeException("Expected 1*8alphanum, has " + bfr.remains() + " alphanum");

        return new LanguageRange.Range(rangeStart, bfr.toStringAndReset());
    }

    @Override
    public AcceptLanguage PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<LanguageRangeWithWeight>();

        if (!(IS_CHAR(bs, '*') || IS_ALPHA(bs))) return new AcceptLanguage(value);

        do {
            var languageRange = LANGUAGE_RANGE(bs, bfr);

            Float weight = null;
            SKIP_OWS(bs);
            if (IS_CHAR(bs, ';')) {
                bs.advance(); SKIP_OWS(bs);
                weight = WEIGHT(bs, bfr);
            }

            value.add(new LanguageRangeWithWeight(languageRange, weight));
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));

        return new AcceptLanguage(value);
    }

    @Override
    public byte[] ENCODE_HEADER(AcceptLanguage header) {
        return new byte[0];
    }

}
