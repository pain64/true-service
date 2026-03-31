package http;

import http.BaseDecoder.Buffer;
import http.BaseDecoder.ByteStream;
import http.HttpParser.Header;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.JumpTables.IS_SCHEME_TABLE;
import static http.JumpTables.IS_UNRESERVED_OR_SUBDELIMS_TABLE;

// UriParser
public class URIParser {

    //URI headers
    // Content-Location, Host, Location, Referer, Via, Origin, AccessControlAllowOrigin

    public sealed interface Host {
        String getValue();
        final class RegName implements Host {
            private final byte[] value;

            public RegName(byte[] value) {
                this.value = value;
            }

            @Override
            public String getValue() {
                return new String(value, StandardCharsets.UTF_8);
            }
        }
        final class IpV4Address implements Host {
            private final byte[] value;

            public IpV4Address(byte[] value) {
                this.value = value;
            }

            @Override
            public String getValue() {
                return new String(value, StandardCharsets.UTF_8);
            }
        }
        final class IpV6Address implements Host {
            private final byte[] value;

            public IpV6Address(byte[] value) {
                this.value = value;
            }

            @Override
            public String getValue() {
                return new String(value, StandardCharsets.UTF_8);
            }
        }
    }


    // URI ~ header
    // create from decoded client -> server
    // create from String server -> client
    // parse

    public static class URI implements Header {
        public final byte[] value;
        private boolean parsed;

        private String scheme;
        private Host host;
        private Integer port;
        private String path;
        private ArrayList<Q> query;

        public void parse() {
            if (!parsed) {
                // ...
                this.parsed = true;
            }
        }

        public URI(byte[] value) {
            this.value = value;

            parse();
        }

        public URI(String value, boolean parse) {
            this.value = value.getBytes(StandardCharsets.UTF_8);

            if (parse) parse();
        }

        public String getScheme() {
            return null;
        }

        public String getHost() {
            return null;
        }

        public int getPort() {
            return 0;
        }

        public String getPath() {
            return null;
        }

    }

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

    public static String IP_V6ADDRESS(ByteStream bs, Buffer bfr) {
        return "";
    }

    public static String IP_LITERAL(ByteStream bs, Buffer bfr) {
        CHAR(bs, '[');
        var ipv6Address = IP_V6ADDRESS(bs, bfr);
        CHAR(bs, ']');

        return ipv6Address;
    }

    public static boolean IP_V4ADDRESS_OPT(ByteStream bs, Buffer bfr) {

        var isV4Address = true;
        var i = 0;
        while (i < 4) {

            var k = 0;
            while (k < 3) {
                if (IS_DIGIT(bs)) {bfr.push(DIGIT(bs)); k++; }
                else break;
            }

            if (k == 0) {isV4Address = false; break;}

            if (
                !(
                    (k == 2 && bfr.getLast(2) >= '1') ||
                        ((k == 3) && (bfr.getLast(3) == '1' || (bfr.getLast(3) == '2' && bfr.getLast(2) <= '5' && bfr.getLast(1) <= '5')))
                )
            ) { isV4Address = false; break; }

            if (i != 3) {
                if (!IS_CHAR(bs, '.')) {isV4Address = false; break;};
                bfr.push(CHAR(bs, '.'));
            }
            i++;
        }

        if (!isV4Address) {
            for (var j = 0; j < bfr.remains(); j++) bs.unadvance(bfr.bytes[bfr.remains()-1-j]);
            bfr.reset();
            return false;
        }

        return true;
    }

    public static String REG_NAME(ByteStream bs, Buffer bfr) {
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
        if (IS_CHAR(bs, '[')) return IP_V6ADDRESS(bs, bfr);
        else if (IP_V4ADDRESS_OPT(bs, bfr)) return bfr.toStringAndReset();
        return REG_NAME(bs, bfr);
    }

    public static int PORT(ByteStream bs) {
        var value = IS_DIGIT(bs) ? DIGIT(bs) - '0' : -1;

        while(IS_DIGIT(bs)) {
            value += value * 10 + (DIGIT(bs) - '0');
        }
        return value;
    }

    public static String AUTHORITY(ByteStream bs, Buffer bfr) {
        return "";
    }

    public static String ABSOLUTE_URI(ByteStream bs, Buffer bfr) {
        var scheme = SCHEME(bs, bfr);
        CHAR(bs, ':');
        return "";
    }
}
