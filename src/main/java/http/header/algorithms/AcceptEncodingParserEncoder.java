package http.header.algorithms;

import http.HttpParser.*;

import java.util.ArrayList;

import static http.Base.*;
import static http.header.DTOs.*;

public class AcceptEncodingParserEncoder implements HeaderParser<AcceptEncoding>, HeaderEncoder<AcceptEncoding> {
    @Override
    public AcceptEncoding PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<EncodingWithWeight>();

        if (!IS_TCHAR(bs)) return new AcceptEncoding(value);

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

            value.add(new EncodingWithWeight(encoding, weight));
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));

        return new AcceptEncoding(value);
    }

    @Override
    public byte[] ENCODE_HEADER(AcceptEncoding header) {
        return new byte[0];
    }
}
