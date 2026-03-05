package http.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.HttpParser.*;
import static http.HttpParser.HeaderParser;
import static http.JumpTables.IS_TCHAR_TABLE;
import static http.header.AcceptCharsetHeader.*;

public class AcceptCharsetHeader implements HeaderParser<AcceptCharset>, HeaderEncoder<AcceptCharset> {

    public sealed interface Charset {
        record Star() implements Charset { }
        record Token(String type) implements Charset { }
    }

    public record CharsetWithWeight (Charset charset, Float weight) {}

    public static class AcceptCharset extends Header {
        public final ArrayList<CharsetWithWeight> value;

        public AcceptCharset(ArrayList<CharsetWithWeight> value) {
            this.value = value;
        }
    }

    @Override
    public AcceptCharset PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<CharsetWithWeight>();

        while (TCHAR_CHECK(bs)) {

            var b = bs.advance();
            Charset charset;

            if (b == '*') charset = new Charset.Star();
            else {
                TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
                charset = new Charset.Token(bfr.toStringAndReset());
            }

            var weightOpt = WEIGHT_OPT(bs, bfr);
            value.add(new CharsetWithWeight(charset, weightOpt == -1 ? null : weightOpt));

            OWS_SYMBOL_OWS_SKIP(bs, ',');
        }
        return new AcceptCharset(value);
    }

    @Override
    public byte[] ENCODE_HEADER(AcceptCharset header) {
        return new byte[0];
    }
}
