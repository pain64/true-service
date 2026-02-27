package http;

import http.Base.ByteStream;

public class JumpTables {
    public static final int TABLE_SIZE = 128;


    // Убрать IS_DIGIT_TABLE
    public static final boolean[] IS_DIGIT_TABLE = new boolean[TABLE_SIZE];
    { for (var i = 0; i < TABLE_SIZE; i++) IS_DIGIT_TABLE[i] = (byte) i >= '0' && (byte) i <= '9'; }

    public static byte DIGIT(ByteStream bs) {
        var b = bs.advance(); if (IS_DIGIT_TABLE[b]) return b;
        throw new RuntimeException("Expected DIGIT");
    }

    public static byte DIGIT_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_DIGIT_TABLE[b]) return b;
        else {bs.unadvance(b); return -1;}
    }

    public static final boolean[] IS_ALPHA_TABLE = new boolean[TABLE_SIZE];
    { for (var i = 0; i < TABLE_SIZE; i++) IS_ALPHA_TABLE[i] = (((byte) i >= 'a' && (byte) i <= 'z') || (byte) i >= 'A' && (byte) i <= 'Z'); }

    public static byte ALPHA(ByteStream bs) {
        var b = bs.advance(); if (IS_ALPHA_TABLE[b]) return b;
        throw new RuntimeException("Expected ALPHA");
    }

    public static byte ALPHA_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_ALPHA_TABLE[b]) return b;
        else {bs.unadvance(b); return -1;}
    }

    // ALPHA/DIGIT

    public static final boolean[] IS_ALPHA_OR_DIGIT_TABLE = new boolean[TABLE_SIZE];
    { for (var i = 0; i < TABLE_SIZE; i++) IS_ALPHA_OR_DIGIT_TABLE[i] = (IS_ALPHA_TABLE[i] || IS_DIGIT_TABLE[i]); }

    public static byte ALPHA_DIGIT(ByteStream bs) {
        var b = bs.advance(); if (IS_ALPHA_OR_DIGIT_TABLE[b]) return b;
        throw new RuntimeException("Expected ALPHA/DIGIT");
    }

    public static byte ALPHA_DIGIT_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_ALPHA_OR_DIGIT_TABLE[b]) return b;
        else {bs.unadvance(b); return -1;}
    }

    //HEXDIG
    public static final boolean[] IS_HEXDIG_TABLE = new boolean[TABLE_SIZE];
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_HEXDIG_TABLE[i] = (b >= '0' && b <= '9') || (b >= 'A' && b <= 'F');
        }
    }

    public static byte HEXDIG(ByteStream bs) {
        byte b = bs.advance(); if (IS_HEXDIG_TABLE[b]) return b;
        throw new RuntimeException("Expected HEXDIG");
    }

    public static byte HEXDIG_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_HEXDIG_TABLE[b]) return b;
        else {bs.unadvance(b); return -1;}
    }

    //VCHAR
    public static final byte[] VCHAR_TABLE = new byte[TABLE_SIZE];
    {
        // new byte[] {"*", "a", "b"}
        for (var i = 0; i < TABLE_SIZE; i++) {
            if ((((byte) i >= '!' && (byte) i <= '~'))) ALPHA_TABLE[i] = (byte) i;
            else ALPHA_TABLE[i] = -1;
        }
    }

    public static final boolean[] IS_VCHAR_TABLE = new boolean[TABLE_SIZE];
    { for (var i = 0; i < TABLE_SIZE; i++) IS_VCHAR_TABLE[i] = VCHAR_TABLE[i] != -1; }

    public static byte VCHAR(ByteStream bs) {
        var b = bs.advance(); if (IS_VCHAR_TABLE[b]) return b;
        throw new RuntimeException("Expected VCHAR");
    }

    public static byte VCHAR_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_VCHAR_TABLE[b]) return b;
        else {bs.unadvance(b); return -1;}
    }

    //DELIMITER - удалить?
    public static final byte[] DELIMITER_TABLE = new byte[TABLE_SIZE];
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            if (b == '"' || b == '(' || b == ')' || b == ',' || b == '/'
                || b == ':' || b == ';' || b == '>' || b == '=' || b == '<'
                || b == '?' || b == '@' || b == '[' || b == '\\' || b == ']'
                || b == '{' || b == '}') DELIMITER_TABLE[i] = b;
            else DELIMITER_TABLE[i] = -1;
        }
    }

    public static final boolean[] IS_DELIMITER_TABLE = new boolean[TABLE_SIZE];
    { for (var i = 0; i < TABLE_SIZE; i++) IS_DELIMITER_TABLE[i] = DELIMITER_TABLE[i] != -1; }

    //TCHAR
    public static final byte[] TCHAR_TABLE = new byte[TABLE_SIZE];
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            if (IS_VCHAR_TABLE[i] && !IS_DELIMITER_TABLE[i]) TCHAR_TABLE[i] = (byte) i;
            else TCHAR_TABLE[i] = -1;
        }
    }

    public static final boolean[] IS_TCHAR_TABLE = new boolean[TABLE_SIZE];
    { for (var i = 0; i < TABLE_SIZE; i++) IS_TCHAR_TABLE[i] = TCHAR_TABLE[i] != -1; }

    public static byte TCHAR(ByteStream bs) {
        var b = bs.advance();
        if (IS_TCHAR_TABLE[b]) return b;
        throw new RuntimeException("Expected TCHAR");
    }

    public static byte TCHAR_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_TCHAR_TABLE[b]) return b;
        else {bs.unadvance(b); return -1;}
    }

    //TCHAR_DQUOTE
    public static final boolean[] IS_TCHAR_DQUOTE_TABLE = new boolean[TABLE_SIZE];
    { for (var i = 0; i < TABLE_SIZE; i++) IS_TCHAR_TABLE[i] = (TCHAR_TABLE[i] != -1 || (byte) i == '"'); }

    public static byte TCHAR_DQUOTE(ByteStream bs) {
        var b = bs.advance();
        if (IS_TCHAR_DQUOTE_TABLE[b]) return b;
        throw new RuntimeException("Expected TCHAR or DQUOTE");
    }

    public static byte TCHAR_DQUOTE_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_TCHAR_DQUOTE_TABLE[b]) return b;
        else {bs.unadvance(b); return -1;}
    }

    //URI:SCHEME
    public static final boolean[] IS_SCHEME_TABLE = new boolean[TABLE_SIZE];
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_SCHEME_TABLE[i] = (
                (b >= 'A' && b <= 'Z') || (b >= 'a' && b <= 'z')
                || (b >= '0' && b <= '9') || b == '+' || b == '-' || b == '.'
            );
        }
    }

    public static byte SCHEME(ByteStream bs) {
        var b = bs.advance(); if (IS_SCHEME_TABLE[b]) return b;
        throw new RuntimeException("Expected SCHEME");
    }

    public static byte SCHEME_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_SCHEME_TABLE[b]) return b;
        else {bs.unadvance(b); return -1;}
    }

    //QDTEXT
    public static final boolean[] IS_QDTEXT_TABLE = new boolean[2*TABLE_SIZE];
    {
        for (var i = 0; i < 2*TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_QDTEXT_TABLE[i] = (b < 0
                    || b == '\t' || b == ' ' || b == '!' || (b >= '#' && b <= '[') || (b >= ']' && b <= '~')
            );
        }
    }

    public static byte QDTEXT_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_QDTEXT_TABLE[b]) return b;
        else { bs.unadvance(b); return 0; }
    }

    //QUOTED PAIR
    public static final boolean[] IS_QUOTED_PAIR_TABLE = new boolean[2*TABLE_SIZE];
    {
        for (var i = 0; i < 2*TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_QDTEXT_TABLE[i] = (b < 0
                || b == '\t' || b == ' '
                || (b >= 0x21 && b <= 0x7E)
            );
        }
    }

    public static byte QUOTED_PAIR_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_QUOTED_PAIR_TABLE[b]) return b;
        else { bs.unadvance(b); return 0; }
    }

    //TOKEN68
    public static final boolean[] IS_TOKEN68_TABLE = new boolean[TABLE_SIZE];
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_TOKEN68_TABLE[i] = (
                (b >= 'a' && b <= 'z') || (b >= 'A' && b <= 'Z') || (b >= '0' && b <= '9')
                || b == '=' || b == '-' || b == '.' || b == '_' || b == '~' || b == '+' || b == '/');
        }
    }

    public static byte TOKEN68_OPT(ByteStream bs) {
        var b = bs.advance();
        if (IS_TOKEN68_TABLE[b]) return b;
        else { bs.unadvance(b); return -1; }
    }

}
