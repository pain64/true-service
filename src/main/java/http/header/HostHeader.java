package http.header;

import static http.Base.*;
import static http.URIParser.IS_SUBDELIM;
import static http.URIParser.IS_UNRESERVED;

public class HostHeader {

    public static String IP_V6ADDRESS(ByteStream bs, Buffer bfr) {

    }

    public static String IP_VFUTURE(ByteStream bs, Buffer bfr) {
        bfr.push(CHAR(bs, 'v')); bfr.push(HEXDIG(bs));
        while (HEXDIG_CHECK(bs)) bfr.push(bs.advance());
        bfr.push(CHAR(bs, '.'));

        var b = bs.advance();

        if (!(IS_UNRESERVED(b) || IS_SUBDELIM(b) || b == ':'))
            throw new RuntimeException("Expected unreserved / subdelim / :");
        b = bs.advance();

        do {
            bfr.push(b);
            b = bs.advance();
        } while (IS_UNRESERVED(b) || IS_SUBDELIM(b) || b == ':');

        return bfr.toStringAndReset();
    }

    public static String IP_LITERAL(ByteStream bs, Buffer bfr) {
        BYTE(bs, '[');

        BYTE(bs, ']');
    }

    public static String IP_V4ADDRESS(ByteStream bs, Buffer bfr) {
        byte b;
        for (var i = 0; i < 4; i++) {
            var k = 0;
            while (k < 3) {
                b = (k == 0) ? DIGIT(bs) : DIGIT_CHECK(bs);
                if (b != -1) bfr.push(b);
                else break;
                k++;
            }
            if (!((k == 2 && bfr.getLast(2) >= '1')
                    || ((k == 3) && (
                        bfr.getLast(3) == '1' || (bfr.getLast(3) == '2' && bfr.getLast(2) <= '5' && bfr.getLast(1) <= '5'))
                        )
            )) {
                throw new RuntimeException("Expected OCTET");
            }
            if (i != 3) bfr.push(BYTE(bs, '.'));
        }

        return bfr.toStringAndReset();
    }

    public static String REG_NAME(ByteStream bs, Buffer bfr) {

    }

    // host        = IP-literal / IPv4address / reg-name
    public static String HOST(ByteStream bs, Buffer bfr) {


    }


}
