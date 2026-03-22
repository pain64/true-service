package http.header.algorithms.Trailer;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class TEParserEncoder implements ValueListHeaderParser<TCoding, TE> {
    @Override
    public TE create(ArrayList<TCoding> valueArray) {
        return new TE(valueArray);
    }

    @Override
    public void decode(ByteStream bs, Buffer bfr, ArrayList<TCoding> dest) {
        if (!IS_TCHAR(bs)) return;

        TCoding tCoding;
        do {
            var parameters = new ArrayList<Parameter>();
            var weight = (Float) null;

            TOKEN_TCHAR(bs, bfr);
            var transferCoding = bfr.toStringAndReset();
            if ("trailers".equals(transferCoding)) tCoding = new TCoding.Trailers();
            else {
                while (OWS_DELIMITER_OWS_SKIP(bs, ';')) {
                    if (IS_CHAR(bs, 'q')) weight = WEIGHT(bs, bfr);
                    else {
                        var paramNameLength = PARAMETER(bs, bfr);
                        var paramName = new String(bfr.bytes, 0, paramNameLength, StandardCharsets.UTF_8);
                        parameters.add(new Parameter(paramName, new String(bfr.bytes, paramNameLength, bfr.remains()-paramNameLength, StandardCharsets.UTF_8)));
                    }
                }
                tCoding = new TCoding.Value(transferCoding, parameters, weight);
            }

            dest.add(tCoding);
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
    }

    @Override
    public void encode(ResponseByteStream rbs, TE header) {
        for (var i = 0; i < header.value.size(); i++) {
            var tCoding = header.value.get(i);

            if (tCoding instanceof TCoding.Trailers) rbs.push("trailers");
            else {
                var v = ((TCoding.Value) tCoding);
                rbs.push(v.transferCoding);
                BaseEncoder.PARAMETERS(rbs, v.parameters);
                BaseEncoder.WEIGHT(rbs, v.weight);
            }

            if (header.value.size()-1 != i) rbs.push(',');
        }
    }
}
