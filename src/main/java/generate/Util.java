package generate;


public class Util {

    public static String getIndentString(int indent) {
        return "\n" + "\t".repeat(indent);
    }

    public static String getLine(String value, int indent) {
        return getIndentString(indent) + value;
    }

    public static String getNormalizedClassName(Class<?> header) {
        return header.getSimpleName().substring(0, 1).toLowerCase() + header.getSimpleName().substring(1);
    }
}
