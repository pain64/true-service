package http.header.algorithms;

import java.util.ArrayList;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class AcceptRangesParserEncoder implements HeaderParserMultiline<AcceptRangeType>, HeaderEncoder<AcceptRanges> {
    @Override
    public void PARSE_HEADER(ByteStream bs, Buffer bfr, ArrayList<AcceptRangeType> toAdd) {
        do {
            TOKEN_TCHAR(bs, bfr);
            var token = bfr.toStringAndReset();

            toAdd.add(
                switch (token) {
                    case "none" -> new AcceptRangeType.None();
                    case "bytes" -> new AcceptRangeType.Bytes();
                    default -> new AcceptRangeType.Token(token);
                });
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, AcceptRanges header) {
        return new byte[0];
    }

}
