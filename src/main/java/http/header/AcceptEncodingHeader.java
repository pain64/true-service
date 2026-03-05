package http.header;

import http.HttpParser.Header;
import http.HttpParser.HeaderEncoder;
import http.HttpParser.HeaderParser;


import java.util.ArrayList;

import static http.Base.*;
import static http.JumpTables.IS_TCHAR_TABLE;
import static http.header.AcceptEncodingHeader.*;

public class AcceptEncodingHeader implements HeaderParser<AcceptEncoding>, HeaderEncoder<AcceptEncoding> {

    public sealed interface Encoding {
        record Star() implements Encoding { }
        record Identity() implements Encoding { }
        record Token(String type) implements Encoding { }
    }

    public record EncodingWithWeight (Encoding encoding, Float weight) {}

    public static class AcceptEncoding extends Header {
        public final ArrayList<EncodingWithWeight> value;

        public AcceptEncoding(ArrayList<EncodingWithWeight> value) {
            this.value = value;
        }
    }

    @Override
    public AcceptEncoding PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<EncodingWithWeight>();

        while (TCHAR_CHECK(bs)) {
            TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
            var token = bfr.toStringAndReset();

            var encoding = switch (token) {
                case "*" -> new Encoding.Star();
                case "indentity" -> new Encoding.Identity();
                default -> new Encoding.Token(token);
            };

            var weightOpt = WEIGHT_OPT(bs, bfr);
            value.add(new EncodingWithWeight(encoding, weightOpt == -1 ? null : weightOpt));

            if (!OWS_SYMBOL_OWS_SKIP(bs, ',')) break;
        }
        return new AcceptEncoding(value);
    }

    @Override
    public byte[] ENCODE_HEADER(AcceptEncoding header) {
        return new byte[0];
    }
}
