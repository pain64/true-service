package http.header.algorithms.Accept;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import http.HttpParser.*;
import http.header.DTOs;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.header.DTOs.*;

public class AcceptLanguageParserEncoder implements ValueListHeaderParser<LanguageRangeWithWeight, AcceptLanguage> {
    public static LanguageRange LANGUAGE_RANGE(ByteStream bs, Buffer bfr) {
        if (IS_CHAR(bs, '*')) {bs.advance(); return new LanguageRange.Star();}

        TOKEN_ALPHA(bs, bfr);

        if (bfr.remains() > 8)
            throw new HeaderDecodeException(bs.position(), "Expected 1*8ALPHA, has " + bfr.remains() + " ALPHA");

        var rangeStart = bfr.toStringAndReset();

        if (!IS_CHAR(bs, '-')) return new LanguageRange.One(rangeStart);
        bs.advance();

        TOKEN_ALPHA_OR_DIGIT(bs, bfr);

        if (bfr.remains() > 8)
            throw new HeaderDecodeException(bs.position(), "Expected 1*8alphanum, has " + bfr.remains() + " alphanum");

        return new LanguageRange.Range(rangeStart, bfr.toStringAndReset());
    }

    @Override
    public AcceptLanguage create(ArrayList<LanguageRangeWithWeight> valueArray) {
        return new AcceptLanguage(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<LanguageRangeWithWeight> dest) {
        if (!(IS_CHAR(bs, '*') || IS_ALPHA(bs))) return;

        do {
            var languageRange = LANGUAGE_RANGE(bs, bfr);

            var weight = (Float) null;
            SKIP_OWS(bs);
            if (IS_CHAR(bs, ';')) {
                bs.advance(); SKIP_OWS(bs);
                weight = WEIGHT(bs, bfr);
            }

            dest.add(new LanguageRangeWithWeight(languageRange, weight));
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
    }

    @Override
    public void encode(ResponseByteStream rbs, AcceptLanguage header) {
        for (var i = 0; i < header.value.size(); i++) {
            var range = header.value.get(i).range;
            var weight = header.value.get(i).weight;

            if (range instanceof LanguageRange.Range.Star) rbs.push('*');
            else if (range instanceof LanguageRange.Range.One) rbs.push(((LanguageRange.One) range).value);
            else {
                rbs.push(((LanguageRange.Range) range).rangeStart);
                rbs.push('-');
                rbs.push(((LanguageRange.Range) range).rangeEnd);
            }

            BaseEncoder.WEIGHT(rbs, weight);

            if (header.value.size()-1 != i) rbs.push(',');
        }
    }
}
