package http.header.algorithms;

import http.BaseEncoder;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class UpgradeParserEncoder implements ValueListHeaderParser<Protocol, Upgrade> {
    @Override
    public Upgrade create(ArrayList<Protocol> valueArray) {
        return new Upgrade(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<Protocol> dest) {
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
    public void encode(BaseEncoder.ResponseByteStream rbs, Upgrade header) {

    }
}
