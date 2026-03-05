package http.header;

import http.Base.Buffer;
import http.Base.ByteStream;

import java.util.ArrayList;

import static http.Base.*;
import static http.HttpParser.*;
import static http.JumpTables.IS_TCHAR_TABLE;
import static http.header.UpgradeHeader.*;

public class UpgradeHeader implements HeaderParser<Upgrade>, HeaderEncoder<Upgrade> {
    public static class Protocol {
        public final String name;
        public final String version;

        public Protocol(String name, String version) {
            this.name = name;
            this.version = version;
        }
    }
    public static class Upgrade extends Header {
        public final ArrayList<Protocol> value;

        public Upgrade(ArrayList<Protocol> value) {
            this.value = value;
        }
    }

    @Override
    public Upgrade PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<Protocol>();

        if (TCHAR_CHECK(bs)) {
            do {
                TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
                if (bfr.remains() == 0) throw new RuntimeException("Expected protocol name");
                var name = bfr.toStringAndReset();

                String version = null;
                if (CHAR_CHECK(bs, '/')) {
                    bs.advance();
                    TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
                    if (bfr.remains() == 0) throw new RuntimeException("Expected protocol version");
                    version = bfr.toStringAndReset();
                }

                value.add(new Protocol(name, version));
            } while (OWS_SYMBOL_OWS_SKIP(bs, ','));
        }

        return new Upgrade(value);
    }

    @Override
    public byte[] ENCODE_HEADER(Upgrade header) {
        return new byte[0];
    }
}
