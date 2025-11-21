package org.example.shortener;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
// import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UrlShortenerApi {

    public int add(int a, int b) {
        return a + b;
    }

    final DataSource ds;

    // POST /UrlShortenerApi.encode { "url": "https://example.com" }
    // JsonBodyParametersReader
     String encode(String url) {
        return "123" + url;
//            return Long.toHexString(
//                ds.q(
//                    "insert into links values(default, ?) returning id", url
//                ).fetchOne(long.class)
//            );
    }

    // GET /UrlShortenerApi.decode/123
    // PathVariableParametersReader
     String decode(String encodedId) {
        return "https://example.com";
//            return ds.q(
//                "select url from links where id = ?",
//                Long.decode("0x" + encodedId)
//            ).fetchOne(String.class);
    }

    public record AB<X, Y>(X x, Y y) {}

    //
    // UrlShortenerApi.ts
    //     type AB<X, Y> = { x: X, y: Y }
    //
    //     function join(
    //         ab: AB<?, string | null>,
    //         list: Array<string | null>,
    //         delimiter: string | null
    //     ) {
    //     }
    //    generator simple version:
    //        see: Server.java:106
    //            - method.getAnnotatedParameterTypes()
    //            - method.getAnnotatedReturnType()
    //            - Статьи про reflection api
    //            - https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AnnotatedType.html
    //            - https://github.com/pain64/true-sql/blob/main/lib/src/main/java/net/truej/sql/compiler/StatementGenerator.java#L58
    //
    //        Map java types -> ts types
    //
    //
    //        f:
    //             parameters: List<AnnotatedType>,
    //             resultType: AnnotatedType         -> String

    public @Nullable String join(
        AB<Map<String, @Nullable String>, @Nullable Integer> ab,
        List<@Nullable String> list,
        @Nullable String delimiter
    ) {
        // join(',', filter(list, s -> s != null))
        // join ',' (filter list \s s != null)
        // join(',', filter(list, isNotNil))
        // join ',' (filter list isNotNil)
        return list.stream().filter(Objects::nonNull)
            .collect(Collectors.joining(delimiter != null ? delimiter : ","));
    }

}
