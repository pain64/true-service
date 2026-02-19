import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.Reader;
import java.util.List;
import java.util.Objects;

public class Test3 {

    // type I = Impl1 | Impl2
    // type Impl1 = {kind: 'Impl1', x: number }
    // type Impl2 = {kind: 'Impl2', y: number }

    // jakson-module-kotlin

    @JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME, property = "kind")
    sealed interface I {}
    record Impl1(@Nullable Integer x) implements I {}
    record Impl2(int y) implements I {}

    record A<T extends Comparable<T>>(T v) { }

    @Test public void test() {

        var x = new ObjectMapper().readValue("""
            {"v": 1}
            """, new TypeReference<A<Integer>>() { });

        var y = new ObjectMapper().readerFor(new TypeReference<A<Integer>>() {});

        System.out.println(
            new ObjectMapper().writerFor(I.class).writeValueAsString(
                new Impl2(42)
            )
        );

        System.out.println(x);
    }
}
