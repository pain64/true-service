package http.parser.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.BYTE_OPT;
import static http.Base.SKIP_OWS;

public class AcceptEncodingParser {

    public sealed interface Encoding {
        record Star() implements Encoding { }
        record Identity() implements Encoding { }
        record Token(String type) implements Encoding { }
    }

    public record EncodingWithWeight (Encoding encoding, Float weight) {}

    public static class AcceptEncoding {
        public final ArrayList<EncodingWithWeight> value;

        public AcceptEncoding(ArrayList<EncodingWithWeight> value) {
            this.value = value;
        }
    }

    // [ ( codings [ weight ] ) *( OWS "," OWS ( codings [weight ] ) ) ]
    public static AcceptEncoding ACCEPT_ENCODING(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<EncodingWithWeight>();
        bfr.reset();

        while (true) {
            byte tcharOpt;

            if ((tcharOpt = TCHAR_OPT(bs)) == -1)
                break;
            else bs.unadvance(tcharOpt);

            TOKEN_OPT(bs, bfr);
            var token = bfr.toStringAndReset();

            var weightOpt = WEIGHT_FROM_BS_OPT(bs, bfr);

            var encoding = switch (token) {
                case "*" -> new Encoding.Star();
                case "indentity" -> new Encoding.Identity();
                default -> new Encoding.Token(token);
            };

            value.add(new EncodingWithWeight(encoding, weightOpt == -1 ? null : weightOpt));

            SKIP_OWS(bs);
            if (BYTE_OPT(bs, ',') != -1) break;
            SKIP_OWS(bs);
        }
        return new AcceptEncoding(value);
    }
}
