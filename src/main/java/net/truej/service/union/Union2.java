package net.truej.service.union;

public sealed interface Union2<T1, T2> {

    record V1<T1, T2>(T1 value) implements Union2<T1, T2> {}
    record V2<T1, T2>(T2 value) implements Union2<T1, T2> {}

    static <T1, T2> Union2<T1, T2> of1(T1 value) {
        return new V1<>(value);
    }

    static <T2, T1> Union2<T1, T2> of2(T2 value) {
        return new V2<>(value);
    }
}
