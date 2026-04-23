package http.parsing.headers;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class ExpectParserEncoder implements ListHeaderParser<Expectation, Expect> {
    @Override
    public Expect create(ArrayList<Expectation> valueArray) {
        return new Expect(valueArray);
    }

    @Override
    public void decode(RequestByteStream bs, Buffer bfr, ArrayList<Expectation> dest) {
        if (!IS_TCHAR(bs)) return;

        do {
            TOKEN_TCHAR(bs, bfr);
            var name = bfr.toStringAndReset();

            var tokenValue = (String) null;
            var parameters = new ArrayList<Parameter>();
            if (IS_CHAR(bs, '=')) {
                bs.advance();
                if (IS_CHAR(bs, '"')) QUOTED_STRING(bs, bfr);
                else TOKEN_TCHAR(bs, bfr);

                tokenValue = bfr.toStringAndReset();
            }

            while (OWS_DELIMITER_OWS_SKIP(bs, ';')) {
                var paramNameLength = PARAMETER(bs, bfr);
                parameters.add(new Parameter(
                    new String(bfr.bytes, 0, paramNameLength, StandardCharsets.UTF_8),
                    new String(bfr.bytes, paramNameLength, bfr.remains()-paramNameLength, StandardCharsets.UTF_8))
                );
            }

            dest.add(new Expectation(name, tokenValue, parameters));
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
    }

    @Override
    public void encode(ResponseByteStream rbs, Expect header) {
        for (var i = 0; i < header.value.size(); i++) {
            var value = header.value.get(i);
            rbs.push(value.name);
            if (value.value != null) {
                rbs.push('=');
                rbs.push('"'); rbs.push(value.value); rbs.push('"');

                BaseEncoder.PARAMETERS(rbs, value.parameters);
            }

            if (i != header.value.size()-1) rbs.push(',');
        }
    }
}
