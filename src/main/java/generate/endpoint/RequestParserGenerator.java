package generate.endpoint;

import generate.searchtree.SearchTreeAlg;
import http.RequestByteStream;
import http.dto.Headers.ContentLength;
import internalapi.MemorySegmentInputStream;
import internalapi.MemorySegmentOutputStream;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.function.Function;

import static generate.Util.*;
import static generate.searchtree.SearchTreeAlg.createSearchTree;

public class RequestParserGenerator {
    public record PathParameter(Class<?> clazz, String paramName, Function<String, String> decoderInvocation) {}
    public record QueryParameter(int id, Class<?> clazz, boolean isNullable, String key, String paramName, Function<String, String> decoderInvocation) {}

    public record HeaderConfiguration(int id, Class<?> clazz, String paramName, boolean isNullable, String httpName, Function<String, String> decoderInvocation) {}
    public record ValueListHeaderConfiguration(int id, Class<?> clazz, String paramName, String httpName, Class<?> paramClazz, Function<String, String> decoderInvocation) {}
    public record CookieConfiguration(int id, Class<?> clazz, String paramName, boolean isNullable, String cookieName, Function<String, String> decoderInvocation) {}

    private final String endpointClassName;

    private final ArrayList<Class<?>> parsers;

    private final String method;
    private final String path;

    private final ArrayList<PathParameter> pathParameters;
    private final ArrayList<QueryParameter> queryParameters;

    private final ArrayList<HeaderConfiguration> valueHeadersCfg;
    private final ArrayList<ValueListHeaderConfiguration> valueListHeadersCfg;
    private final ArrayList<CookieConfiguration> cookieHeaderCfg;

    private final static int COOKIE_HEADER_MAGIC_ID = 111_222;
    private final static String QUERY_PARAM_SEARCH_FUNCTION_NAME = "searchQueryParam";
    private final static String HEADERS_SEARCH_FUNCTION_NAME = "searchHeader";
    private final static String COOKIE_SEARCH_FUNCTION_NAME = "searchCookie";

    public RequestParserGenerator(
        String endpointClassName,
        String method,
        String path,
        ArrayList<Class<?>> parsers,
        ArrayList<PathParameter> pathParameters,
        ArrayList<QueryParameter> queryParameters,
        ArrayList<HeaderConfiguration> valueHeadersCfg,
        ArrayList<ValueListHeaderConfiguration> valueListHeadersCfg,
        ArrayList<CookieConfiguration> cookieHeaderCfg) {

        this.endpointClassName = endpointClassName;

        this.method = method;
        this.path = path;

        this.parsers = parsers;

        this.pathParameters = pathParameters;
        this.queryParameters = queryParameters;

        this.valueHeadersCfg = valueHeadersCfg;
        this.valueListHeadersCfg = valueListHeadersCfg;
        this.cookieHeaderCfg = cookieHeaderCfg;

    }

    // PATH PARAMETERS
    public String getPathParametersDeclareCode() {
        var code = "";

        for (var pathParam : pathParameters) {
            code += getLine(pathParam.clazz.getSimpleName() + " " + pathParam.paramName + " = null;", 0);
        }
        return code;
    }

    public String getPathParametersParseCode() {
        var code = "";

        var firstParam = pathParameters.getFirst();
        code += getLine(firstParam.decoderInvocation.apply(firstParam.paramName), 1);

        for (var i = 1; i < pathParameters.size(); i++) {
            var pathParam = pathParameters.get(i);
            code += getLine("CHAR(rbs, '\\\\');", 0);
            code += getLine(pathParam.decoderInvocation.apply(pathParam.paramName), 1);
        }

        return code;
    }


    // QUERY PARAMETERS
    public String getQueryParametersDeclareCode() {
        var code = "";

        for (var queryParam : queryParameters) {
            code += getLine(queryParam.clazz.getSimpleName() + " " + queryParam.paramName + " = null;", 0);
        }
        return code;
    }

    static class QueryParamSearch implements SearchTreeAlg.SearchString {
        final QueryParameter value;
        public QueryParamSearch(QueryParameter value) {
            this.value = value;
        }

        @Override
        public String getString() {
            return value.key + "=";
        }

        @Override
        public int getId() {
            return value.id;
        }
    }

    public String getQueryParametersSearchFunctionCode() {
        var code = "";
        code += getLine("private static int " + QUERY_PARAM_SEARCH_FUNCTION_NAME + "(RequestByteStream rbs) {", 0);

        var searchQueryParams = new ArrayList<>(queryParameters.stream().map(QueryParamSearch::new).toList());
        var tree = createSearchTree(searchQueryParams);

        code += SearchTreeAlg.generateSearchTreeCode(tree, 1);

        code += getLine("return -1;", 0);
        code += getLine("}", 0);

        return code;
    }

    public String getQueryParametersParseCode() {
        var code = "";

        code += getLine("do {", 0);

        code += getLine("switch (" + QUERY_PARAM_SEARCH_FUNCTION_NAME + "(rbs)) {", 1);

        for (var q : queryParameters) {
            code += getLine("case " + q.id + ":", 2);
            code += getLine(q.decoderInvocation.apply(q.paramName), 2);
            code += getLine("break;", 2);
        }
        code += getLine("default:", 2);

        code += getLine("}", 1);

        code += getLine("} while (AMPERSAND_SKIP(rbs));", 0);
        return code;
    }

    public String getQueryParamsCheckNullCode() {
        var code = "";
        var notNullQueryParams = queryParameters.stream().filter(q -> !q.isNullable).toList();
        if (notNullQueryParams.isEmpty()) return code;

        code += getLine("if (", 0);

        var first = true;
        for (var queryParam: queryParameters.stream().filter(q -> !q.isNullable).toList()) {
            var prefix = first ? "" : "|| ";
            first = false;
            code += getLine(prefix + queryParam.paramName + " == null", 1);
        }

        code += getLine(") {", 0);
        code += getLine("ArrayList<String> queryParamsNotFound = new ArrayList<>();", 1);
        for (var queryParam: queryParameters.stream().filter(q -> !q.isNullable).toList()) {
            code += getLine("if (" + queryParam.paramName + " == null) queryParamsNotFound.add(\"" + queryParam.key +"\");", 1);
        }

        // TODO
        code += getLine("// response 400 with info;", 1);
        code += getLine("}", 0);

        return code;
    }

    // HEADERS REQUEST
    private static String getValueListHeaderParamName(ValueListHeaderConfiguration header) {
        return header.paramName + "Value";
    }

    public String getHeadersDeclareCode() {
        var code = "";

        for (var header: valueHeadersCfg) {
            var headerClassName = header.clazz.getSimpleName();
            var headerParamName = header.paramName;
            var headerDeclarationCode = headerClassName + " " + headerParamName + " = null;";
            code += getLine(headerDeclarationCode, 0);
        }

        for (var header: valueListHeadersCfg) {
            var headerClassName = header.clazz.getSimpleName();
            var headerParamName = header.paramName;
            var headerDeclarationCode = headerClassName + " " + headerParamName + " = null;";
            code += getLine(headerDeclarationCode, 0);

            var valueListDeclarationCode = "ArrayList<" + header.paramClazz.getSimpleName() + "> "
                + getValueListHeaderParamName(header)
                + " = new ArrayList<>();";
            code += getLine(valueListDeclarationCode, 0);
        }

        for (var cookie: cookieHeaderCfg) {
            var headerClassName = cookie.clazz.getSimpleName();
            var headerParamName = cookie.paramName;
            var headerDeclarationCode = headerClassName + " " + headerParamName + " = null;";
            code += getLine(headerDeclarationCode, 0);
        }

        return code;
    }

    static class HeaderSearch implements SearchTreeAlg.SearchString {
        final HeaderConfiguration value;
        public HeaderSearch(HeaderConfiguration value) {
            this.value = value;
        }

        @Override
        public String getString() {
            return value.httpName + ":";
        }

        @Override
        public int getId() {
            return value.id;
        }
    }

    static class ValueListHeaderSearch implements SearchTreeAlg.SearchString {
        final ValueListHeaderConfiguration value;
        public ValueListHeaderSearch(ValueListHeaderConfiguration value) {
            this.value = value;
        }

        @Override
        public String getString() {
            return value.httpName + ":";
        }

        @Override
        public int getId() {
            return value.id;
        }
    }

    static class CookieHeaderSearch implements SearchTreeAlg.SearchString {
        ArrayList<CookieConfiguration> cookies;

        public CookieHeaderSearch(ArrayList<CookieConfiguration> cookies) {
            this.cookies = cookies;
        }

        @Override
        public String getString() {
            return "Cookie:";
        }

        @Override
        public int getId() {
            return COOKIE_HEADER_MAGIC_ID;
        }
    }

    static class CookieSearch implements SearchTreeAlg.SearchString {
        final CookieConfiguration value;

        public CookieSearch(CookieConfiguration value) {
            this.value = value;
        }

        @Override
        public String getString() {
            return value.cookieName + "=";
        }

        @Override
        public int getId() {
            return value.id;
        }
    }

    public String getHeadersSearchFunctionCode() {
        var code = "";
        code += getLine("private static int " + HEADERS_SEARCH_FUNCTION_NAME + "(RequestByteStream rbs) {", 0);

        ArrayList<SearchTreeAlg.SearchString> allHeaders = new ArrayList<>();

        allHeaders.addAll(valueHeadersCfg.stream().map(HeaderSearch::new).toList());
        allHeaders.addAll(valueListHeadersCfg.stream().map(ValueListHeaderSearch::new).toList());
        allHeaders.add(new CookieHeaderSearch(cookieHeaderCfg));

        var tree = createSearchTree(allHeaders);

        var searchCode = SearchTreeAlg.generateSearchTreeCode(tree, 1);
        code += searchCode;

        code += getLine("return -1;", 0);
        code += getLine("}", 0);

        return code;
    }

    public String getCookieSearchFunctionCode() {
        var code = "";
        code += getLine("private static int " + COOKIE_SEARCH_FUNCTION_NAME + "(RequestByteStream rbs) {", 0);

        ArrayList<SearchTreeAlg.SearchString> allCookie = new ArrayList<>();
        allCookie.addAll(cookieHeaderCfg.stream().map(CookieSearch::new).toList());

        var tree = createSearchTree(allCookie);

        code += SearchTreeAlg.generateSearchTreeCode(tree, 1);

        code += getLine("return -1;", 0);
        code += getLine("}", 0);

        return code;
    }

    public String getHeadersParsingCode() {
        var code = "";

        ArrayList<Object> allHeaders = new ArrayList<>();

        allHeaders.addAll(valueHeadersCfg);
        allHeaders.addAll(valueListHeadersCfg);

        code += getLine("while (!IS_CHAR(rbs, '\\r')) {", 0);
        code += getLine("switch (" + HEADERS_SEARCH_FUNCTION_NAME + "(rbs)) {", 1);

        for (var header : allHeaders) {
            switch (header) {
                case HeaderConfiguration h -> {
                    code += getLine("case " + h.id + ":", 2);
                    var line = h.paramName + " = " + h.decoderInvocation.apply(h.paramName);
                    code += getLine(line, 2);
                }
                case ValueListHeaderConfiguration h -> {
                    code += getLine("case " + h.id + ":", 2);
                    var line = h.decoderInvocation.apply(getValueListHeaderParamName(h));
                    code += getLine(line, 2);
                }
                default -> {
                }
            }
            code += getLine("break;", 2);
        }

        if (!cookieHeaderCfg.isEmpty()) {
            code += getLine("case " + COOKIE_HEADER_MAGIC_ID + ":", 2);
            code += getLine("do {", 3);
            code += getLine("switch (" + COOKIE_SEARCH_FUNCTION_NAME + "(rbs)) {", 4);

            for (var cookie : cookieHeaderCfg) {
                code += getLine("case " + cookie.id + ":", 5);
                code += getLine(cookie.decoderInvocation.apply(cookie.paramName) , 6);
                code += getLine("break;" , 6);
            }
            code += getLine("default: ", 5);

            code += getLine("}", 4);
            code += getLine("} while (OWS_DELIMITER_OWS_SKIP(rbs, ';'));", 2);
            code += getLine("break;", 2);
        }

        //TODO:
        code += getLine("default:", 2);
        code += getLine("}", 1);
        code += getLine("SKIP_TO_CRLF(rbs);", 1);
        code += getLine("}", 0);

        return code;
    }

    public String getHeadersCheckNullCode() {
        var code = "";
        var notNullValueHeaders = valueHeadersCfg.stream().filter(h -> !h.isNullable).toList();
        var notNullCookie = cookieHeaderCfg.stream().filter(c -> !c.isNullable).toList();
        if (notNullValueHeaders.isEmpty() && notNullCookie.isEmpty()) return code;

        code += getLine("if (", 0);

        var first = true;
        for (var header: valueHeadersCfg.stream().filter(h -> !h.isNullable).toList()) {
            var prefix = first ? "" : "|| ";
            first = false;
            code += getLine(prefix + header.paramName + " == null", 1);
        }

        for (var cookie: cookieHeaderCfg.stream().filter(c -> !c.isNullable).toList()) {
            var prefix = first ? "" : "|| ";
            first = false;
            code += getLine(prefix + cookie.paramName + " == null", 1);
        }

        code += getLine(") {", 0);
        code += getLine("ArrayList<String> headersNotFound = new ArrayList<>();", 1);
        for (var header: valueHeadersCfg.stream().filter(h -> !h.isNullable).toList()) {
            code += getLine("if (" + header.paramName + " == null) headersNotFound.add(\"" + header.httpName +"\");", 1);
        }

        code += getLine("ArrayList<String> cookieNotFound = new ArrayList<>();", 1);
        for (var cookie: cookieHeaderCfg.stream().filter(c -> !c.isNullable).toList()) {
            code += getLine("if (" + cookie.paramName + " == null) cookieNotFound.add(\"" + cookie.cookieName +"\");", 1);
        }
        // TODO
        code += getLine("// response 400 with info;", 1);
        code += getLine("}", 0);

        return code;
    }

    public String getValueListHeadersInitCode() {
        var code = "";
        for (var header : valueListHeadersCfg) {
            code += getLine(header.paramName + " = new " + header.clazz.getSimpleName() + "(" + getValueListHeaderParamName(header) + ");", 0);
        }

        return code;
    }


    // BODY REQUEST
    public abstract static class AA {
        protected final byte[] bytes;
        protected final MemorySegment ms;

        public AA(byte[] bytes, MemorySegment ms) {
            this.bytes = bytes;
            this.ms = ms;
        }
    }

    public static class TextPlain extends AA {
        public TextPlain(byte[] bytes, MemorySegment ms) {
            super(bytes, ms);
        }

        // может быть конструктор
        public String decode(MemorySegmentInputStream is, ContentLength contentLength) {
            var bytesPosition = 0;
            var ms = (MemorySegment) null;

            while ((ms = is.read()) != null) {
                for (var i = 0; i < ms.byteSize();) bytes[bytesPosition++] = ms.getAtIndex(ValueLayout.JAVA_BYTE, i++);
            }

            return new String(bytes);
        }

        public void encode(MemorySegmentOutputStream os, String value) {
            var position = 0;
            var count = 0;
            var valueBytes = value.getBytes(StandardCharsets.UTF_8);

            while (count < value.length()) {
                var length = 0;
                while (position < ms.byteSize()) {
                    ms.setAtIndex(ValueLayout.JAVA_BYTE, position++, valueBytes[count++]);
                    length++;
                }
                os.push(ms.asSlice(0, length)); //TODO as slice
                position = 0;
            }
        }
    }


    // HEADERS RESPONSE


    // BODY RESPONSE

    public String getParsersInitCode() {
        var code = "";

        for (var parser: parsers) {
            var declareLine = "private static final " + parser.getSimpleName() + " " + getNormalizedClassName(parser)
                + " = new " + parser.getSimpleName() + "();";
            code += getLine(declareLine, 1);
        }
        return code;
    }

    public String generateEndpointClass() {
        var code = "";

        code += getLine("public class " + endpointClassName + " {", 0);

        code += getLine("private static final String method = \"" + method + "\";", 0);
        code += getLine("private static final String path = \"" + path + "\";", 0); code += getLine("", 0);
        code += getLine("private static final int MAX_BUFFER_SIZE = 2048;", 0); code += getLine("", 0);

        code += getParsersInitCode(); code += getLine("", 0);
        code += getQueryParametersSearchFunctionCode(); code += getLine("", 0);
        code += getHeadersSearchFunctionCode(); code += getLine("", 0);
        code += getCookieSearchFunctionCode(); code += getLine("", 0);

        code += getLine("public void invoke(RequestByteStream rbs) {", 1); code += getLine("", 1);
        code += getLine("var bfr = new Buffer(MAX_BUFFER_SIZE);", 1); code += getLine("", 1);
        // URI section
        code += getLine("rbs.movePosition(method.length() + path.length() + 1);", 1);

        if (!pathParameters.isEmpty()) {
            code += getPathParametersDeclareCode(); code += getLine("", 1);
            code += getPathParametersParseCode(); code += getLine("", 1);
        }

        if (!queryParameters.isEmpty()) {
            // has non null
            code += getQueryParametersDeclareCode(); code += getLine("", 1);

            if (queryParameters.stream().map(q -> !q.isNullable).reduce(false, (q1, q2) -> q1 || q2)) {
                code += getLine("CHAR(rbs, '?');", 1);
                code += getQueryParametersParseCode(); code += getLine("", 2);
            } else {
                code += getLine("if (IS_CHAR(rbs, '?')) {", 1);
                code += getQueryParametersParseCode(); code += getLine("", 2);
                code += getLine("}", 1);
            }

            code += getQueryParamsCheckNullCode(); code += getLine("", 1);
        } else {
            code += getLine("SKIP_TO_SP(rbs);", 1);
        }
        // HTTP version section
        code += getLine("CHAR(rbs, ' ');", 1);
        code += getLine("CHAR(rbs, 'H'); CHAR(rbs, 'T'); CHAR(rbs, 'T'); CHAR(rbs, 'P'); CHAR(rbs, '/');", 1);
        code += getLine("int httpMajorDigit = NDIGIT(rbs, 1);", 1);
        code += getLine("CHAR(rbs, '.');", 1);
        code += getLine("int httpMinorDigit = NDIGIT(rbs, 1);", 1);code += getLine("", 1);
        // some logic to forward http 1.1

        // HEADERS parsing section
        code += getLine("CRLF(rbs);", 1);code += getLine("", 1);

        if (!valueHeadersCfg.isEmpty() || !valueListHeadersCfg.isEmpty() || !cookieHeaderCfg.isEmpty()) {
            code += getHeadersDeclareCode(); code += getLine("", 1);
            code += getHeadersParsingCode(); code += getLine("", 1);
            code += getHeadersCheckNullCode(); code += getLine("", 1);
            code += getValueListHeadersInitCode(); code += getLine("", 1);
        }

        // BODY parsing section
        code += getLine("CRLF(rbs);", 1);

        var rbs = new RequestByteStream(null);

            // create memory segment input stream
        code += getLine("var sis = new InitSegmentInputStream(null, rbs.getMsTail());", 1);

            // init handlers. Content-Encoding // transfer encoding chunked. http specific

            // init variable
        code += getLine("var sis = new InitSegmentInputStream(null, rbs.getMsTail());", 1);
            // write body parse expression

            // INVOCATION section
                // call code

        // SWITCH result

            // request-line section

            // HEADERS RESPONSE section

            // BODY RESPONSE section

        code += getLine("}", 1);
        code += getLine("}", 0);

        return code;
    }

    public interface StatusCode { }

    public sealed interface ValueDestination {
        record Body() implements ValueDestination {}
        record Header(Class<?> headerClass) implements ValueDestination {}
    }

    public static class FieldMapper {
        public final String paramName;
        public final ValueDestination valueDestination;

        public FieldMapper(String fieldName, ValueDestination valueDestination) {
            this.paramName = fieldName;
            this.valueDestination = valueDestination;
        }
    }

    public sealed interface Once {}

    public sealed interface ResultType {
        record Scalar(StatusCode statusCode, ValueDestination valueDestination) implements ResultType, Once {}
        record Struct(StatusCode statusCode, ArrayList<FieldMapper> fieldMappers) implements ResultType, Once {}
        record Union(ArrayList<Once> options) implements ResultType {}
    }

    public String generateScalarResultCode(ResultType.Scalar value) {
        var code = "";
        code += getLine("respBS.push(\"HTTP/1.1 \".getBytes(StandardCharsets.UTF_8));", 1);
        code += getLine("NUMBER(respBS, statusCode);", 1);


        return code;
    }

    public String generateStructResultCode(ResultType.Struct value) {
        var code = "";


        return code;
    }

}
