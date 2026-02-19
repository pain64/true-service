package json;

import java.util.Iterator;

public interface AsJsonArray<B> {
    <T> B decode(Iterable<T> elements);
    Iterator<?> encode(B value);
}
