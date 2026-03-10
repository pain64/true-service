package http.header.algorithms;

import http.HttpParser.*;

import java.util.ArrayList;

import static http.BaseParser.*;
import static http.header.DTOs.*;

public class AcceptEncodingParserEncoder implements HeaderParserMultiline<EncodingWithWeight>, HeaderEncoder<AcceptEncoding> {
    @Override
    public void PARSE_HEADER(ByteStream bs, Buffer bfr, ArrayList<EncodingWithWeight> toAdd) {
        if (!IS_TCHAR(bs)) return;

        do {
            TOKEN_TCHAR(bs, bfr);
            var token = bfr.toStringAndReset();

            var encoding = switch (token) {
                case "*" -> new Encoding.Star();
                case "indentity" -> new Encoding.Identity();
                default -> new Encoding.Token(token);
            };

            Float weight = null;
            SKIP_OWS(bs);
            if (IS_CHAR(bs, ';')) {
                bs.advance(); SKIP_OWS(bs);
                weight = WEIGHT(bs, bfr);
            }

            toAdd.add(new EncodingWithWeight(encoding, weight));
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, AcceptEncoding header) {
        return new byte[0];
    }

}
