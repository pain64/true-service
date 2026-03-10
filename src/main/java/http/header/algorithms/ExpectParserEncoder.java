package http.header.algorithms;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ExpectParserEncoder implements HeaderParser<Expect>, HeaderEncoder<Expect> {
    @Override
    public Expect PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<Expectation>();

        if (!IS_TCHAR(bs)) return new Expect(value);

        do {
            TOKEN_TCHAR(bs, bfr);
            var name = bfr.toStringAndReset();

            String tokenValue = null;
            var parameters = new ArrayList<Parameter>();
            if (IS_CHAR(bs, '=')) {
                bs.advance();
                if (IS_CHAR(bs, '"')) QUOTED_STRING(bs, bfr);
                else TOKEN_TCHAR(bs, bfr);

                tokenValue = bfr.toStringAndReset();
            }

            while (OWS_SYMBOL_OWS_SKIP(bs, ';')) {
                var paramNameLength = PARAMETER(bs, bfr);
                parameters.add(new Parameter(
                    new String(bfr.bytes, 0, paramNameLength, StandardCharsets.UTF_8),
                    new String(bfr.bytes, paramNameLength, bfr.remains(), StandardCharsets.UTF_8))
                );
            }

            value.add(new Expectation(name, tokenValue, parameters));
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));

        return new Expect(value);
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, Expect header) {
        return new byte[0];
    }

}
