package http.parser.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.SKIP_OWS;
import static http.JumpTables.IS_TCHAR_TABLE;
import static http.JumpTables.TCHAR_OPT;

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

        // #()
        // var isNextRequired = false;
        // do {
        //    if (IS_END_OF_HEADER(bs, isNextRequired)) {
        //        return value;
        //
        //    if(*) { WEIGHT() }
        //    else {
        //        TOKEN(); WEIGHT();
        //    }
        //
        //    isNextRequired = true;
        // } while(',')

        while (true) {

            byte b = bs.advance();
            if (IS_TCHAR_TABLE[b]) bs.unadvance(b); else break;

            TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);

            if (bfr.remains() == 1 && bfr.bytes[0] != '*')
                throw new RuntimeException("Expected *");

            var token = bfr.toStringAndReset();

            var weightOpt = WEIGHT_FROM_BS_OPT(bs, bfr);
            var weight = weightOpt == -1 ? null : weightOpt;

            value.add("*".equals(token) ?
                new CharsetWithWeight(new Charset.Star(), weight) :
                new CharsetWithWeight(new Charset.Token(token), weight));

            OWS_SYMBOL_OWS_SKIP(bs, ',');
        }
        return new AcceptCharset(value);
    }
}
