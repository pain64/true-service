package http;

import java.util.UUID;

public class Dsl2 {
    @interface StatusCode {}
    @interface MimeType {}
    @interface Header {}
    @interface QueryParameter {}

    @MimeType @interface XWwwFormUrlencoded {}
    @MimeType @interface ApplicationJson {}
    @MimeType @interface TextPlain {}

    @StatusCode @interface Sc200 {}
    @StatusCode @interface Sc400 {}
    @StatusCode @interface Sc500 {}
    @StatusCode @interface Sc511 {}

    record Authorization() {}

    String endpoint1(int a, int b) {
        return "";
    }

    // Union2<
    //     @Sc200 @TextPlain String,
    //     @Sc511 @TextPlain String
    // > endpoint() {
    //     if (!condition) return Union2.of2("an error occurred")
    //     return Union2.of1("hello");
    // }

    @Sc400 @TextPlain String hello() {
        return "hello";
    }

    @Sc400 @TextPlain String endpoint1(
        @QueryParameter     UUID          id,
        @Header             Authorization authorization,
        @XWwwFormUrlencoded int           a,
        @XWwwFormUrlencoded int           b
    ) {
        return "";
    }
}
