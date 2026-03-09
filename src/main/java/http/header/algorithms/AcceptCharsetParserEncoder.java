package http.header.algorithms;

import java.util.ArrayList;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class AcceptCharsetParserEncoder implements HeaderParserArray<CharsetWithWeight>, HeaderEncoder<AcceptCharset> {
    @Override
    public ArrayList<CharsetWithWeight> PARSE_HEADER(ByteStream bs, Buffer bfr, ArrayList<CharsetWithWeight> toAdd) {
        return null;
    }

    @Override
    public AcceptCharset PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<CharsetWithWeight>();

        if (!IS_TCHAR(bs)) return new AcceptCharset(value);

        do {
            Charset charset;

            if (IS_CHAR(bs, '*')) charset = new Charset.Star();
            else {
                TOKEN_TCHAR(bs, bfr);
                charset = new Charset.Token(bfr.toStringAndReset());
            }

            Float weight = null;
            SKIP_OWS(bs);
            if (IS_CHAR(bs, ';')) {
                bs.advance(); SKIP_OWS(bs);
                weight = WEIGHT(bs, bfr);
            }

            value.add(new CharsetWithWeight(charset, weight));
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));

        return new AcceptCharset(value);
    }

    @Override
    public byte[] ENCODE_HEADER(AcceptCharset header) {
        return new byte[0];
    }

}
