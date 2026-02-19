package json;

public interface AsJsonObject1<B> {
    record JsonObject1<M1>(M1 m1v) {}
    <M1> B decode(M1 member1);
    JsonObject1<?> encode(B value);
}
