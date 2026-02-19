package json;

public interface AsJsonObjectUnion2<B, D, V1 extends B, V2 extends B> {
    int discriminate(D kind);
}
