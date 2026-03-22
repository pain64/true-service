package http;

public class JumpTables {
    public static final int TABLE_SIZE = 128;
    // ~ 1кб кеша

    //ALPHA попадет в кеш
    public static final boolean[] IS_ALPHA_TABLE = new boolean[TABLE_SIZE];
    static
    { for (var i = 0; i < TABLE_SIZE; i++) IS_ALPHA_TABLE[i] = (((byte) i >= 'a' && (byte) i <= 'z') || (byte) i >= 'A' && (byte) i <= 'Z'); }

    // ALPHA/DIGIT попадет в кеш
    public static final boolean[] IS_ALPHA_OR_DIGIT_TABLE = new boolean[TABLE_SIZE];
    static
    { for (var i = 0; i < TABLE_SIZE; i++) IS_ALPHA_OR_DIGIT_TABLE[i] = (IS_ALPHA_TABLE[i] || ((byte) i >= '0' && (byte) i <= '9')); }

    //HEXDIG попадет в кеш
    public static final boolean[] IS_HEXDIG_TABLE = new boolean[TABLE_SIZE];
    static
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_HEXDIG_TABLE[i] = (b >= '0' && b <= '9') || (b >= 'A' && b <= 'F');
        }
    }

    //VCHAR не попадает в кеш?
    public static final boolean[] IS_VCHAR_TABLE = new boolean[TABLE_SIZE];
    static
    { for (var i = 0; i < TABLE_SIZE; i++) IS_VCHAR_TABLE[i] = ((byte) i >= '!' && (byte) i <= '~'); }

    //DELIMITER не попадает в кеш?
    public static final boolean[] IS_DELIMITER_TABLE = new boolean[TABLE_SIZE];
    static
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_DELIMITER_TABLE[i] = (b == '"' || b == '(' || b == ')' || b == ',' || b == '/'
                || b == ':' || b == ';' || b == '>' || b == '=' || b == '<'
                || b == '?' || b == '@' || b == '[' || b == '\\' || b == ']'
                || b == '{' || b == '}');
        }
    }

    //TCHAR попадет в кеш
    public static final boolean[] IS_TCHAR_TABLE = new boolean[TABLE_SIZE];
    static
    { for (var i = 0; i < TABLE_SIZE; i++) IS_TCHAR_TABLE[i] = IS_VCHAR_TABLE[i] && !IS_DELIMITER_TABLE[i]; }

    //URI:SCHEME попадет в кеш
    public static final boolean[] IS_SCHEME_TABLE = new boolean[TABLE_SIZE];
    static
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_SCHEME_TABLE[i] = (
                (b >= 'A' && b <= 'Z') || (b >= 'a' && b <= 'z')
                || (b >= '0' && b <= '9') || b == '+' || b == '-' || b == '.'
            );
        }
    }

    //CTEXT попадет в кеш
    public static final boolean[] IS_UNRESERVED_OR_SUBDELIMS_TABLE = new boolean[TABLE_SIZE];
    static
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_UNRESERVED_OR_SUBDELIMS_TABLE[i] = (
                (b >= 'A' && b <= 'Z') || (b >= 'a' && b <= 'z') || (b >= '0' && b <= '9') //UNRESERVED
                    || b == '-' || b == '.' || b == '_' || b == '~' //UNRESERVED
                    || b == '!' || b == '$' || b == '&' || b == '\'' || b == '(' || b == ')' //SUBDELIM
                    || b == '*' || b == '+' || b == ',' || b == ';' || b == '='); //SUBDELIM
        }
    }

    //QDTEXT попадет в кеш
    public static final boolean[] IS_QDTEXT_TABLE = new boolean[TABLE_SIZE];
    static
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_QDTEXT_TABLE[i] = (b == '\t' || b == ' ' || b == '!' || (b >= '#' && b <= '[') || (b >= ']' && b <= '~')
            );
        }
    }

    //QUOTED PAIR попадет в кеш
    public static final boolean[] IS_QUOTED_PAIR_TABLE = new boolean[TABLE_SIZE];
    static
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_QUOTED_PAIR_TABLE[i] = (b == '\t' || b == ' ' || (b >= 0x21 && b <= 0x7E));
        }
    }

    //TOKEN68 попадет в кеш
    public static final boolean[] IS_TOKEN68_TABLE = new boolean[TABLE_SIZE];
    static
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_TOKEN68_TABLE[i] = (
                (b >= 'a' && b <= 'z') || (b >= 'A' && b <= 'Z') || (b >= '0' && b <= '9')
                || b == '=' || b == '-' || b == '.' || b == '_' || b == '~' || b == '+' || b == '/');
        }
    }

    //CTEXT попадет в кеш
    public static final boolean[] IS_CTEXT_TABLE = new boolean[TABLE_SIZE];
    static
    {
        for (var i = 0; i < TABLE_SIZE; i++) {
            var b = (byte) i;
            IS_CTEXT_TABLE[i] = (b == '\t' || b == ' '
                || (b >= '!' && b <= '\'') || (b >= '*' && b <= '[') || (b >= ']' && b <= '~'));
        }
    }
}
