package json.impl;

import json.AsJsonObjectUnion2;

public class IfaceJsonBinding implements
    AsJsonObjectUnion2<Iface, String, IfaceImpl1, IfaceImpl2> {

    @Override public int discriminate(String kind) {
        return switch (kind) {
            case "iface1" -> 1;
            case "iface2" -> 2;
            default -> throw new RuntimeException("bad kind");
        };
    }
}
