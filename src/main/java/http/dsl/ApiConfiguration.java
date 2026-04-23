package http.dsl;

public class ApiConfiguration extends
    ApiConfigurationBase<Void, ApiConfiguration.MethodConfiguration> {

    public static class MethodConfiguration
        extends MethodConfigurationBase<ApiConfiguration> { }

//     public class UrlShortenerApi {
//         static {
//             new ApiConfiguration()
//                 .method("decode")
//                 /**/.httpMethod(GET)
//                 /**/.parameterFromPath("id")
//                 /**/.resultScalar()
//                 /*    */.toHeader("Location")
//         }
//
//         public String decode(String id) {
//             return ...
//         }
//     }

    // Module / Api / Method (endpoint)
    // ""/""/""
    // нужен ли нам TrueServiceConfiguration ???
    //
}
