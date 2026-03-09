package http;

import http.Base.Buffer;
import http.Base.ByteStream;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static http.Base.*;
import static http.JumpTables.IS_SCHEME_TABLE;
import static http.JumpTables.IS_UNRESERVED_OR_SUBDELIMS_TABLE;

public class URI {

    public static String SCHEME(ByteStream bs, Buffer bfr) {
        bfr.push(ALPHA(bs));

        byte b;
        while (IS_SCHEME_TABLE[(b = bs.advance())]) {bfr.push(b);}
        bs.unadvance(b);

        return bfr.toStringAndReset();
    }

    public static String USERINFO(ByteStream bs, Buffer bfr) {
        byte b = bs.advance();
        while (b == '%' || IS_UNRESERVED_OR_SUBDELIMS_TABLE[b] || b == ':') {
            bfr.push(b);
            if (b == '%') {
                bfr.push(HEXDIG(bs)); bfr.push(HEXDIG(bs));
            }
            b = bs.advance();
        } bs.unadvance(b);

        return bfr.toStringAndReset();
    }

    public static InetAddress IP_V6ADDRESS(ByteStream bs, Buffer bfr) {

    }

    public static InetAddress IP_LITERAL(ByteStream bs, Buffer bfr) {
        CHAR(bs, '[');
        var ipv6Address = IP_V6ADDRESS(bs, bfr);
        CHAR(bs, ']');

        return ipv6Address;
    }

    public static InetAddress IP_V4ADDRESS(ByteStream bs) throws UnknownHostException {
        byte[] inetAddressBytes = new byte[]{0,0,0,0};

        for (var i = 0; i < 4; i++) {
            var k = 0;
            while (k < 3) {
                if (k == 0 || DIGIT_CHECK(bs)) {
                    if (k == 0) inetAddressBytes[i] = (byte) (DIGIT(bs) - '0');
                    else inetAddressBytes[i] = (byte) ((inetAddressBytes[i] * 10) + (DIGIT(bs) - '0'));
                    k++;
                }
                else break;
            }

            if (i != 3) CHAR(bs, '.');
        }

        return InetAddress.getByAddress(inetAddressBytes);
    }

    public static InetAddress REG_NAME(ByteStream bs, Buffer bfr) {
        byte b = bs.advance();
        while (b == '%' || IS_UNRESERVED_OR_SUBDELIMS_TABLE[b]) {
            bfr.push(b);
            if (b == '%') {
                bfr.push(HEXDIG(bs)); bfr.push(HEXDIG(bs));
            }
            b = bs.advance();
        } bs.unadvance(b);

        return bfr.toStringAndReset();
    }

    public static String HOST(ByteStream bs, Buffer bfr) {

    }

    public static int PORT(ByteStream bs) {
        var value = DIGIT_CHECK(bs) ? DIGIT(bs) - '0' : -1;

        while(DIGIT_CHECK(bs)) {
            value += value * 10 + (DIGIT(bs) - '0');
        }
        return value;
    }

    public static String AUTHORITY(ByteStream bs, Buffer bfr) {

    }

    public static String ABSOLUTE_URI(ByteStream bs, Buffer bfr) {
        var scheme = SCHEME(bs, bfr);
        CHAR(bs, ':');
    }
}
