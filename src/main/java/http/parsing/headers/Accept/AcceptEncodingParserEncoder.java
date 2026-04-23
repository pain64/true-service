package http.parsing.headers.Accept;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ListHeaderParser;

import java.util.ArrayList;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class AcceptEncodingParserEncoder implements ListHeaderParser<EncodingWithWeight, AcceptEncoding> {
    @Override
    public AcceptEncoding create(ArrayList<EncodingWithWeight> valueArray) {
        return new AcceptEncoding(valueArray);
    }

    @Override
    public void decode(RequestByteStream bs, Buffer bfr, ArrayList<EncodingWithWeight> dest) {
        if (!IS_TCHAR(bs)) return; // if encoding list is empty

        do {
            TOKEN_TCHAR(bs, bfr);
            var token = bfr.toStringAndReset();

            var encoding = switch (token) {
                case "*" -> new Encoding.Star();
                case "identity" -> new Encoding.Identity();
                default -> new Encoding.Token(token);
            };

            var weight = (Float) null;
            SKIP_OWS(bs);
            if (IS_CHAR(bs, ';')) {
                bs.advance(); SKIP_OWS(bs);
                weight = WEIGHT(bs, bfr);
            }

            dest.add(new EncodingWithWeight(encoding, weight));
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
    }

    @Override
    public void encode(ResponseByteStream rbs, AcceptEncoding header) {
        for (var i = 0; i < header.value.size(); i++) {
            var encoding = header.value.get(i).encoding;
            var weight = header.value.get(i).weight;

            if (encoding instanceof Encoding.Star) rbs.push('*');
            else if (encoding instanceof Encoding.Identity) rbs.push("identity");
            else rbs.push(((Encoding.Token) encoding).type);

            BaseEncoder.WEIGHT(rbs, weight);

            if (header.value.size()-1 != i) rbs.push(',');
        }
    }
}
