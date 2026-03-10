package http.header.algorithms;

import java.util.ArrayList;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class UpgradeParserEncoder implements HeaderParser<Upgrade>, HeaderEncoder<Upgrade> {
    @Override
    public Upgrade PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<Protocol>();

        if (IS_TCHAR(bs)) {
            do {
                TOKEN_TCHAR(bs, bfr);
                if (bfr.remains() == 0) throw new RuntimeException("Expected protocol name");
                var name = bfr.toStringAndReset();

                String version = null;
                if (IS_CHAR(bs, '/')) {
                    bs.advance();
                    TOKEN_TCHAR(bs, bfr);
                    if (bfr.remains() == 0) throw new RuntimeException("Expected protocol version");
                    version = bfr.toStringAndReset();
                }

                value.add(new Protocol(name, version));
            } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
        }

        return new Upgrade(value);
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, Upgrade header) {
        return new byte[0];
    }
}
