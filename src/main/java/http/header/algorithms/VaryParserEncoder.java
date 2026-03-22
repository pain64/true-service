package http.header.algorithms;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import http.header.DTOs;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class VaryParserEncoder implements HeaderParser<Vary> {
    @Override
    public Vary decode(ByteStream bs, Buffer bfr) {
        VaryType varyType;
        if (!IS_TCHAR(bs)) return new Vary(new VaryType.Fields(new ArrayList<String>()));
        else if (IS_CHAR(bs, '*')) {bs.advance(); varyType = new VaryType.Star();}
        else {
            var value = new ArrayList<String>();

            do {
                TOKEN_TCHAR(bs, bfr);
                value.add(bfr.toStringAndReset());
            } while (OWS_DELIMITER_OWS_SKIP(bs, ','));

            varyType = new VaryType.Fields(value);
        }

        return new Vary(varyType);
    }

    @Override
    public void encode(ResponseByteStream rbs, Vary header) {
        if (header.value instanceof VaryType.Star) rbs.push('*');
        else BaseEncoder.TOKENS_COMMA_SEPARATED(rbs, ((VaryType.Fields)header.value).value);
    }
}
