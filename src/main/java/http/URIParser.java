package http;

import static http.Base.*;

public class URIParser {
    // locator // origin + path
    // query parameters
    // parse path parameters

    public static boolean IS_UNRESERVED(byte b) {
        return (b >= 'a' && b <= 'z') || (b >= 'A' && b <= 'Z') || (b >= '0' && b <= '9')
            || b == '-' || b == '.' || b == '_' || b == '~';
    }

    public static boolean IS_GENDELIM(byte b) {
        return b == ':' || b == '/' || b == '?' || b == '#' || b == '[' || b == ']' || b == '@';
    }

    public static boolean IS_SUBDELIM(byte b) {
        return b == '!' || b == '$' || b == '&' || b == '\'' || b == '(' || b == ')'
            || b == '*' || b == '+' || b == ',' || b == ';' || b == '=';
    }

    public static String SCHEME(ByteStream bs, Buffer bfr) {
        bfr.reset();

        bfr.push(ALPHA(bs));

        byte b;
        while ((b = SCHEME_CHAR_OPT(bs)) != -1) bfr.push(b);
        return bfr.toStringAndReset();
    }

    // "//" authority path-abempty / path-absolute / path-rootless / path-empty
    public static String HIER_PART(ByteStream bs, Buffer bfr) {

    }

}
