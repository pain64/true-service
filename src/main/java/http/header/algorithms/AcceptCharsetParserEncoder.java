package http.header.algorithms;

import http.BaseEncoder;

import java.net.URI;
import java.util.ArrayList;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

// interface HeaderParser<H extends Header> {
//     H encode(ByteStream bs, Buffer bfr);
//     void decode(...);
// }

// 1. Добавить bs.current() для LL(1) случаев
// 2. Float weight = null; -> var weight = (Float) null;
// 3. UpgradeParserEncoder: parse - переместить if в начало
// 4. все методы парсеров в encode и decode
// 5. выбрасывание ошибок:
//     1. HeaderDecodeException (bs.position(), message)
// 6.
//    interface Header {} // marker interface
//    interface ValueListHeader<V> extends Header {
//       ArrayList<V> values()
//    }
//
//    interface ValueListHeaderEncoder<V, H extends ValueList<V>> {
//        H<V> create();
//        void decode(ByteStream bs, Buffer bfr, ArrayList<V> dest);
//    }
//
// 7. URI - path parameter parser, query parameter parser
// 8. Fast-path на деревьях как static-final
// 9. CORS-headers
// 10.
//

public class AcceptCharsetParserEncoder implements HeaderParserMultiline<CharsetWithWeight>, HeaderEncoderMultiline<AcceptCharset> {
    @Override
    public void PARSE_HEADER(ByteStream bs, Buffer bfr, ArrayList<CharsetWithWeight> toAdd) {
        
        if (!IS_TCHAR(bs)) return; // if accept list is empty

        do {
            final Charset charset;

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
        } while (OWS_DELIMITER_OWS_SKIP(bs, ','));
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
