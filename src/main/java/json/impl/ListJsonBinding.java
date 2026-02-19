package json.impl;

import json.AsJsonArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListJsonBinding implements AsJsonArray<List<?>> {
    // outside at codegen we making cast to List<T>
    @Override public <T> List<?> decode(Iterable<T> elements) {
        var list = new ArrayList<T>();
        for (var el : elements) list.add(el);
        return list;
    }
    @Override public Iterator<?> encode(List<?> list) {
        return list.iterator();
    }
}
