package http.header.algorithms;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class TEParserEncoder implements HeaderParser<TE>, HeaderEncoder<TE> {
    @Override
    public TE PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<TCoding>();
        if (!IS_TCHAR(bs)) return new TE(value);

        TCoding tCoding;
        do {
            var parameters = new ArrayList<Parameter>();
            Float weight = null;

            TOKEN_TCHAR(bs, bfr);
            var transferCoding = bfr.toStringAndReset();
            if ("trailers".equals(transferCoding)) tCoding = new TCoding.Trailers();
            else {
                while (OWS_SYMBOL_OWS_SKIP(bs, ';')) {
                    if (IS_CHAR(bs, 'q')) weight = WEIGHT(bs, bfr);
                    else {
                        var paramNameLength = PARAMETER(bs, bfr);
                        var paramName = new String(bfr.bytes, 0, paramNameLength, StandardCharsets.UTF_8);
                        parameters.add(new Parameter(paramName, new String(bfr.bytes, paramNameLength, bfr.remains(), StandardCharsets.UTF_8)));
                    }
                }
                tCoding = new TCoding.Value(transferCoding, parameters, weight);
            }

            value.add(tCoding);
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));
        return null;
    }

    @Override
    public byte[] ENCODE_HEADER(TE header) {
        return new byte[0];
    }
}
