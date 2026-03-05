package http.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.HttpParser.*;
import static http.JumpTables.IS_TCHAR_TABLE;
import static http.header.AcceptRangesHeader.*;

public class AcceptRangesHeader implements HeaderParser<AcceptRanges>, HeaderEncoder<AcceptRanges> {

    public sealed interface Range {
        record None() implements Range { }
        record Bytes() implements Range { }
        record Token(String value) implements Range { }
    }

    public static class AcceptRanges extends Header {
        public final ArrayList<Range> value;

        public AcceptRanges(ArrayList<Range> value) {
            this.value = value;
        }
    }

    @Override
    public AcceptRanges PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<Range>();

        do {
            if (!TCHAR_CHECK(bs)) throw new RuntimeException("Expected range-unit");

            TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
            var token = bfr.toStringAndReset();

            value.add(
                switch (token) {
                    case "none" -> new Range.None();
                    case "bytes" -> new Range.Bytes();
                    default -> new Range.Token(token);
                });
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));
        return new AcceptRanges(value);
    }

    @Override
    public byte[] ENCODE_HEADER(AcceptRanges header) {
        return new byte[0];
    }
}
