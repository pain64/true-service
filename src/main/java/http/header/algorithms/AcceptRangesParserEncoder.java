package http.header.algorithms;

import java.util.ArrayList;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class AcceptRangesParserEncoder implements HeaderParser<AcceptRanges>, HeaderEncoder<AcceptRanges> {
    @Override
    public AcceptRanges PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<AcceptRangeType>();

        do {
            TOKEN_TCHAR(bs, bfr);
            var token = bfr.toStringAndReset();

            value.add(
                switch (token) {
                    case "none" -> new AcceptRangeType.None();
                    case "bytes" -> new AcceptRangeType.Bytes();
                    default -> new AcceptRangeType.Token(token);
                });
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));
        return new AcceptRanges(value);
    }

    @Override
    public byte[] ENCODE_HEADER(AcceptRanges header) {
        return new byte[0];
    }
}
