package json.impl;

import json.AsJsonObject2;

public class PairJsonBinding implements AsJsonObject2<Pair<?, ?>> {
    @Override public <M1, M2> Pair<M1, M2> decode(M1 k, M2 v) {
        return new Pair<>(k, v);
    }
    @Override public JsonObject2<?, ?> encode(Pair<?, ?> value) {
        return new JsonObject2<>(value.a(), value.b());
    }
}
