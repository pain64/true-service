package http.header.algorithms;

import http.BaseEncoder;
import http.header.DTOs;

import java.util.ArrayList;

import static http.BaseParser.*;
import static http.BaseEncoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class AcceptCharsetParserEncoder implements HeaderParserMultiline<CharsetWithWeight>, HeaderEncoderMultiline<AcceptCharset> {
    @Override
    public void PARSE_HEADER(ByteStream bs, Buffer bfr, ArrayList<CharsetWithWeight> toAdd) {
        if (!IS_TCHAR(bs)) return;

        do {
            Charset charset;

            if (IS_CHAR(bs, '*')) charset = new Charset.Star();
            else {
                TOKEN_TCHAR(bs, bfr);
                charset = new Charset.Token(bfr.toStringAndReset());
            }

            Float weight = null;
            SKIP_OWS(bs);
            if (IS_CHAR(bs, ';')) {
                bs.advance(); SKIP_OWS(bs);
                weight = WEIGHT(bs, bfr);
            }

            toAdd.add(new CharsetWithWeight(charset, weight));
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));
    }

    @Override
    public void ENCODE_HEADER(BaseEncoder.ResponseByteStream rbs, Buffer bfr, AcceptCharset h) {
//        while (true) {
//            pushString(rbs, "Accept-Charset:");
//
//            for (var v : h.value) {
//                if (v.charset instanceof Charset.Star) rbs.push('*');
//                else if (v.charset instanceof Charset.Token) pushString(rbs, ((Charset.Token) v.charset).type());
//
//                if (v.weight != null) {
//                    rbs.push(';');
//                    rbs.push('q');
//                    rbs.push('=');
//                    for (var wch : String.valueOf(v.weight).getBytes()) {
//                        rbs.push(wch);
//                    }
//                }
//                if (rbs.count() > 1024) throw new RuntimeException("Max header line exceeded");
//                rbs.push(',');
//            }
//
//            rbs.push('\r'); rbs.push('\n');
//        }
    }

}
