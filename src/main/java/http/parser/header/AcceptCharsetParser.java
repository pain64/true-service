package http.parser.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.SKIP_OWS;

public class AcceptCharsetParser {

    public sealed interface Charset {
        record Star() implements Charset { }
        record Token(String type) implements Charset { }
    }

    public record CharsetWithWeight (Charset charset, Float weight) {}

    public static class AcceptCharset {
        public final ArrayList<CharsetWithWeight> value;

        public AcceptCharset(ArrayList<CharsetWithWeight> value) {
            this.value = value;
        }
    }

    public static AcceptCharset ACCEPT_CHARSET(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<CharsetWithWeight>();
        bfr.reset();

        while (true) {
            byte tcharOpt;

            if ((tcharOpt = TCHAR_OPT(bs)) == -1)
                break;
            else bs.unadvance(tcharOpt);

            TOKEN_OPT(bs, bfr);

            if (bfr.remains() == 1 && bfr.bytes[0] != '*')
                throw new RuntimeException("Expected *");

            var token = bfr.toStringAndReset();

            var weightOpt = WEIGHT_FROM_BS_OPT(bs, bfr);
            var weight = weightOpt == -1 ? null : weightOpt;

            value.add("*".equals(token) ?
                new CharsetWithWeight(new Charset.Star(), weight) :
                new CharsetWithWeight(new Charset.Token(token), weight));

            SKIP_OWS(bs);
            if (BYTE_OPT(bs, ',') != -1) break;
            SKIP_OWS(bs);
        }
        return new AcceptCharset(value);
    }
}
