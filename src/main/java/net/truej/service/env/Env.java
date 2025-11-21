package net.truej.service.env;

public class Env {
    public static String string(String variableName) {
        var v = System.getenv(variableName);
        if (v == null)
            throw new RuntimeException("required env...");
        return v;
    }

    public static int integer(String variableName) {
        var v = System.getenv(variableName);
        if (v == null)
            throw new RuntimeException("required env...");
        return Integer.parseInt(v);
    }
}
