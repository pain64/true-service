package http.header.algorithms;

import java.util.ArrayList;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class VaryParserEncoder implements HeaderParser<Vary>, HeaderEncoder<Vary> {
    @Override
    public Vary PARSE_HEADER(ByteStream bs, Buffer bfr) {
        VaryType varyType;
        if (!IS_TCHAR(bs)) varyType = new VaryType.Empty();
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
    public void ENCODE_HEADER(ResponseByteStream rbs, Vary header) {
        return new byte[0];
    }
}
