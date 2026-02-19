package json.impl;

import json.AsJsonObject1;

public class ValueHolderJsonBinding implements AsJsonObject1<ValueHolder<?>> {
    // {"x": "string generic version"}
    @Override public <M1> ValueHolder<?> decode(M1 x) {
        return new ValueHolder<>(x);
    }
    @Override public JsonObject1<?> encode(ValueHolder<?> value) {
        return new JsonObject1<>(value.x());
    }
}
