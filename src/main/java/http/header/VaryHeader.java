package http.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.Buffer;
import static http.Base.ByteStream;
import static http.HttpParser.*;
import static http.JumpTables.IS_TCHAR_TABLE;

public class VaryHeader implements HeaderParser<VaryHeader.Vary>, HeaderEncoder<VaryHeader.Vary> {
    public sealed interface VaryType {
        record Empty() implements VaryType {}
        record Star() implements VaryType {}
        record Fields(ArrayList<String> value) implements VaryType {}
    }
    public static class Vary extends Header {
        public final VaryType value;

        public Vary(VaryType value) {
            this.value = value;
        }
    }

    @Override
    public Vary PARSE_HEADER(ByteStream bs, Buffer bfr) {
        VaryType varyType;
        if (CHAR_CHECK(bs, '*')) {bs.advance(); varyType = new VaryType.Star();}
        else if (TCHAR_CHECK(bs)){
            var value = new ArrayList<String>();
            do {
                TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
                value.add(bfr.toStringAndReset());
            } while (OWS_SYMBOL_OWS_SKIP(bs, ','));

            varyType = new VaryType.Fields(value);
        } else varyType = new VaryType.Empty();

        return new Vary(varyType);
    }

    @Override
    public byte[] ENCODE_HEADER(Vary header) {
        return new byte[0];
    }
}
