package json;

public interface AsJsonObject2<B> {
    record JsonObject2<M1, M2>(M1 m1v, M2 m2v) {}
    <M1, M2> B decode(M1 member1, M2 member2);
    JsonObject2<?, ?> encode(B value);
}
