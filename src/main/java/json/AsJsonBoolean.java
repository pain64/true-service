package json;

public interface AsJsonBoolean<T> {
    T decode(boolean jsonValue);
    boolean decode(T value);
}
