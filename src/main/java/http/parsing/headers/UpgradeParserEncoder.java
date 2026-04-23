package http.parsing.headers;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.api.ListHeaderParser;

import java.util.ArrayList;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class UpgradeParserEncoder implements ListHeaderParser<Protocol, Upgrade> {
    @Override
    public Upgrade create(ArrayList<Protocol> valueArray) {
        return new Upgrade(valueArray);
    }

    @Override
    public void decode(RequestByteStream bs, Buffer bfr, ArrayList<Protocol> dest) {
        if (!IS_TCHAR(bs)) return;

        do {
            TOKEN_TCHAR(bs, bfr);
            var name = bfr.toStringAndReset();

            var version = (String) null;
            if (IS_CHAR(bs, '/')) {
                bs.advance();
                TOKEN_TCHAR(bs, bfr);
                version = bfr.toStringAndReset();
            }

            dest.add(new Protocol(name, version));
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
    }

    @Override
    public void encode(ResponseByteStream rbs, Upgrade header) {

    }
}
