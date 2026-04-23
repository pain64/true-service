package http.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class Headers {
    public static class Parameter {
        public final String name;
        public final String value;

        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public sealed interface MediaRangeType {
        final class StarStar implements MediaRangeType { }
        final class TokenStar implements MediaRangeType {
            public final String type;

            public TokenStar(String type) {
                this.type = type;
            }
        }
        final class TokenToken implements MediaRangeType {
            public final String type;
            public final String subtype;

            public TokenToken(String type, String subtype) {
                this.type = type;
                this.subtype = subtype;
            }
        }
    }

    public static class MediaRange {
        public final MediaRangeType mediaRange;
        public final ArrayList<Parameter> parameters;
        public final Float weight;

        public MediaRange(MediaRangeType mediaRange, ArrayList<Parameter> parameters, Float weight) {
            this.mediaRange = mediaRange;
            this.parameters = parameters;
            this.weight = weight;
        }
    }

    public static class Accept {
        public static final String httpName = "Accept";

        public final ArrayList<MediaRange> value;

        public Accept(ArrayList<MediaRange> value) {
            this.value = value;
        }
    }

    public sealed interface Charset {
        final class Star implements Charset { }
        final class Token implements Charset {
            public final String token;

            public Token(String token) {
                this.token = token;
            }
        }
    }

    public static class CharsetWithWeight {
        public final Charset charset;
        public final Float weight;

        public CharsetWithWeight(Charset charset, Float weight) {
            this.charset = charset;
            this.weight = weight;
        }
    }

    public static class AcceptCharset {
        public static final String httpName = "Accept-Charset";

        public final ArrayList<CharsetWithWeight> value;

        public AcceptCharset(ArrayList<CharsetWithWeight> value) {
            this.value = value;
        }
    }

    public sealed interface Encoding {
        final class Star implements Encoding { }
        final class Identity implements Encoding { }
        final class Token implements Encoding {
            public final String type;

            public Token(String type) {
                this.type = type;
            }
        }
    }

    public static class EncodingWithWeight {
        public final Encoding encoding;
        public final Float weight;

        public EncodingWithWeight(Encoding encoding, Float weight) {
            this.encoding = encoding;
            this.weight = weight;
        }
    }

    public static class AcceptEncoding {
        public static final String httpName = "Accept-Encoding";

        public final ArrayList<EncodingWithWeight> value;

        public AcceptEncoding(ArrayList<EncodingWithWeight> value) {
            this.value = value;
        }
    }

    public sealed interface LanguageRange {
        final class Star implements LanguageRange { }
        final class One implements LanguageRange {
            public final String value;

            public One(String value) {
                this.value = value;
            }
        }
        final class Range implements LanguageRange {
            public final String rangeStart;
            public final String rangeEnd;

            public Range(String rangeStart, String rangeEnd) {
                this.rangeStart = rangeStart;
                this.rangeEnd = rangeEnd;
            }
        }
    }

    public static class LanguageRangeWithWeight {
        public final LanguageRange range;
        public final Float weight;

        public LanguageRangeWithWeight(LanguageRange range, Float weight) {
            this.range = range;
            this.weight = weight;
        }
    }

    public static class AcceptLanguage {
        public static final String httpName = "Accept-Language";

        public final ArrayList<LanguageRangeWithWeight> value;

        public AcceptLanguage(ArrayList<LanguageRangeWithWeight> value) {
            this.value = value;
        }
    }

    public static class AcceptRanges {
        public static final String httpName = "Accept-Ranges";
    }

    public sealed interface Method {
        record Get(String httpName) implements Method { }
        record Head(String httpName) implements Method { }
        record Post(String httpName) implements Method { }
        record Put(String httpName) implements Method { }
        record Delete(String httpName) implements Method { }
        record Connect(String httpName) implements Method { }
        record Options(String httpName) implements Method { }
        record Trace(String httpName) implements Method { }
        record Patch(String httpName) implements Method { }
    }

    public static class Allow {
        public static final String httpName = "Allow";

        public final ArrayList<Method> value;

        public Allow(ArrayList<Method> value) {
            this.value = value;
        }
    }

    public static class AuthParam {
        public final String name;
        public final String value;

        public AuthParam(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class AuthenticationInfo {
        public static final String httpName = "Authentication-Info";

        public final ArrayList<AuthParam> value;

        public AuthenticationInfo(ArrayList<AuthParam> value) {
            this.value = value;
        }
    }

    public static class Authorization {
        public static final String httpName = "Authorization";

        public final String authSchema;
        public final String token;
        public final ArrayList<AuthParam> authParams;

        public Authorization(String authSchema, String token, ArrayList<AuthParam> authParams) {
            if (token != null && !authParams.isEmpty()) throw new RuntimeException("Expect token or authParams, not both.");
            this.authSchema = authSchema;
            this.token = token;
            this.authParams = authParams;
        }
    }

    public static class Connection {
        public static final String httpName = "Connection";

        public final ArrayList<String> value;

        public Connection(ArrayList<String> value) {
            this.value = value;
        }
    }

    public static class ContentEncoding {
        public static final String httpName = "Content-Encoding";

        public final ArrayList<String> value;

        public ContentEncoding(ArrayList<String> value) {
            this.value = value;
        }
    }

    public static class ContentLength {
        public static final String httpName = "Content-Length";

        public final int value;

        public ContentLength(int value) {
            this.value = value;
        }
    }

    public sealed interface RangeUnit {
        final class Bytes implements RangeUnit { }
        final class Token implements RangeUnit {
            public final String value;

            public Token(String value) {
                this.value = value;
            }
        }
    }

    public static class Product {
        public final String name;
        public final String version;
        public final String comment;

        public Product(String name, String version, String comment) {
            this.name = name;
            this.version = version;
            this.comment = comment;
        }
    }

    public sealed interface ContentRangeType {
        final class Star implements ContentRangeType { }
        final class Value implements ContentRangeType {
            public final long value;

            public Value(long value) {
                this.value = value;
            }
        }
        final class Interval implements ContentRangeType {
            public final long from;
            public final long to;

            public Interval(long from, long to) {
                if (from > to) {
                    throw new RuntimeException("Required to > from");
                }
                this.from = from;
                this.to = to;
            }
        }
    }

    public static class ContentRange {
        public static final String httpName = "Content-Range";

        public final RangeUnit rangeUnit;
        public final ContentRangeType range;
        public final ContentRangeType size;

        public ContentRange(RangeUnit rangeUnit, ContentRangeType range, ContentRangeType size) {
            this.rangeUnit = rangeUnit;
            this.range = range;
            this.size = size;
        }
    }

    public static class ContentType {
        public static final String httpName = "Content-Type";

        public final String type;
        public final String subtype;
        public final ArrayList<Parameter> value;

        public ContentType(String type, String subtype, ArrayList<Parameter> value) {
            this.type = type;
            this.subtype = subtype;
            this.value = value;
        }
    }

    public static class Date {
        public static final String httpName = "Date";

        public final LocalDateTime value;

        public Date(LocalDateTime value) {
            this.value = value;
        }
    }

    public sealed interface EntityTag {
        final class Default implements EntityTag {
            public final String value;

            public Default(String value) {
                this.value = value;
            }
        }
        final class Weak implements EntityTag {
            public final String value;

            public Weak(String value) {
                this.value = value;
            }
        }
    }

    public sealed interface IfRangeType {
        final class EntityTag implements IfRangeType {
            public final Headers.EntityTag value;

            public EntityTag(Headers.EntityTag value) {
                this.value = value;
            }
        }
        final class Date implements IfRangeType {
            public final LocalDateTime value;

            public Date(LocalDateTime value) {
                this.value = value;
            }
        }
    }

    public static class ETag {
        public static final String httpName = "ETag";

        public final EntityTag value;

        public ETag(EntityTag value) {
            this.value = value;
        }
    }

    public sealed interface MatchEntitiesTags {
        final class All implements MatchEntitiesTags {}
        final class EntitiesTags implements MatchEntitiesTags {
            public final ArrayList<EntityTag> value;

            public EntitiesTags(ArrayList<EntityTag> value) {
                this.value = value;
            }
        }
    }

    public static class IfMatch {
        public static final String httpName = "If-Match";

        public final MatchEntitiesTags value;

        public IfMatch(MatchEntitiesTags value) {
            this.value = value;
        }
    }

    public static class Expectation {
        public final String name;
        public final String value;
        public final ArrayList<Parameter> parameters;

        public Expectation(String name, String value, ArrayList<Parameter> parameters) {
            this.name = name;
            this.value = value;
            this.parameters = parameters;
        }
    }

    public static class Expect {
        public static final String httpName = "Expect";

        public final ArrayList<Expectation> value;

        public Expect(ArrayList<Expectation> value) {
            this.value = value;
        }
    }

    public static class IfModifiedSince extends Date {
        public static final String httpName = "If-Modified-Since";

        public IfModifiedSince(LocalDateTime date) {
            super(date);
        }
    }

    public static class IfNoneMatch extends IfMatch {
        public static final String httpName = "If-None-Match";

        public IfNoneMatch(MatchEntitiesTags value) {
            super(value);
        }
    }

    public static class IfRange {
        public static final String httpName = "If-Range";

        public final IfRangeType value;

        public IfRange(IfRangeType value) {
            this.value = value;
        }
    }

    public static class IfUnmodifiedSince extends Date {
        public static final String httpName = "If-Unmodified-Since";

        public IfUnmodifiedSince(LocalDateTime date) {
            super(date);
        }
    }

    public static class LastModified extends Date {
        public static final String httpName = "Last-Modified";

        public LastModified(LocalDateTime date) {
            super(date);
        }
    }

    public static class MaxForwards {
        public static final String httpName = "Max-Forwards";

        public final long value;

        public MaxForwards(long value) {
            this.value = value;
        }
    }

    public static class Challenge extends Authorization {
        public Challenge(String authSchema, String token, ArrayList<AuthParam> authPararms) {
            super(authSchema, token, authPararms);
        }
    }

    public static class Authenticate {
        public static final String httpName = "Authenticate";

        public final ArrayList<Challenge> value;

        public Authenticate(ArrayList<Challenge> value) {
            this.value = value;
        }
    }

    public static class ProxyAuthenticate extends Authenticate {
        public static final String httpName = "Proxy-Authenticate";

        public ProxyAuthenticate(ArrayList<Challenge> value) {
            super(value);
        }
    }

    public static class ProxyAuthenticationInfo extends AuthenticationInfo {
        public static final String httpName = "Proxy-Authentication-Info";

        public final ArrayList<AuthParam> value;

        public ProxyAuthenticationInfo(ArrayList<AuthParam> value) {
            super(this.value = value);
        }
    }

    public static class ProxyAuthorization extends Authorization {
        public static final String httpName = "Proxy-Authorization";

        public ProxyAuthorization(Authorization auth) {
            super(auth.authSchema, auth.token, auth.authParams);
        }
    }

    public sealed interface RangeSpec  {
        final class Start implements RangeSpec {
            public final long value;

            public Start(long value) {
                this.value = value;
            }
        }
        final class Interval implements RangeSpec {
            public final long from;
            public final long to;

            public Interval(long from, long l) {
                this.from = from;
                to = l;
            }
        }
        final class Suffix implements RangeSpec {
            public final long value;

            public Suffix(long value) {
                this.value = value;
            }
        }
    }

    public static class Range {
        public static final String httpName = "Range";

        public final RangeUnit rangeUnit;
        public final ArrayList<RangeSpec> value;

        public Range(RangeUnit rangeUnit, ArrayList<RangeSpec> value) {
            this.rangeUnit = rangeUnit;
            this.value = value;
        }
    }

    public sealed interface RetryAfterType {
        final class HttpDate extends Date implements RetryAfterType{

            public HttpDate(LocalDateTime value) {
                super(value);
            }
        }
        final class DelaySeconds implements RetryAfterType {
            public final long value;

            public DelaySeconds(long value) {
                this.value = value;
            }
        }
    }

    public static class RetryAfter {
        public static final String httpName = "Retry-After";

        public final RetryAfterType value;

        public RetryAfter(RetryAfterType value) {
            this.value = value;
        }
    }

    public static class Server {
        public static final String httpName = "Server";

        public final ArrayList<Product> value;

        public Server(ArrayList<Product> value) {
            this.value = value;
        }
    }

    public sealed interface TCoding {
        final class Trailers implements TCoding {}
        final class Value implements TCoding {
            public final String transferCoding;
            public final ArrayList<Parameter> parameters;
            public final Float weight;

            public Value(String transferCoding, ArrayList<Parameter> parameters, Float weight) {
                this.transferCoding = transferCoding;
                this.parameters = parameters;
                this.weight = weight;
            }
        }
    }

    public static class TE {
        public static final String httpName = "TE";

        public final ArrayList<TCoding> value;

        public TE(ArrayList<TCoding> value) {
            this.value = value;
        }
    }

    public static class Protocol {
        public final String name;
        public final String version;

        public Protocol(String name, String version) {
            this.name = name;
            this.version = version;
        }
    }

    public static class Upgrade {
        public static final String httpName = "Upgrade";

        public final ArrayList<Protocol> value;

        public Upgrade(ArrayList<Protocol> value) {
            this.value = value;
        }
    }

    public static class UserAgent {
        public static final String httpName = "User-Agent";

        public final ArrayList<Product> value;

        public UserAgent(ArrayList<Product> value) {
            this.value = value;
        }
    }

    public sealed interface VaryType {
        final class Star implements VaryType {}
        final class Fields implements VaryType {
            public final ArrayList<String> value;

            public Fields(ArrayList<String> value) {
                this.value = value;
            }
        }
    }

    public static class Vary {
        public static final String httpName = "Vary";

        public final VaryType value;

        public Vary(VaryType value) {
            this.value = value;
        }
    }

    public static class WWWAuthenticate extends Authenticate {
        public static final String httpName = "WWW-Authenticate";

        public WWWAuthenticate(ArrayList<Challenge> value) {
            super(value);
        }
    }

    public static class AccessControlAllowCredentials {
        public static final String httpName = "Access-Control-Allow-Credentials";
    }

    public static class AccessControlExposeHeaders {
        public static final String httpName = "Access-Control-Expose-Headers";

        public final ArrayList<String> fieldNames;

        public AccessControlExposeHeaders(ArrayList<String> fieldNames) {
            this.fieldNames = fieldNames;
        }
    }

    public static class AccessControlMaxAge {
        public static final String httpName = "Access-Control-Max-Age";
        public final long value;

        public AccessControlMaxAge(long value) {
            this.value = value;
        }
    }

    public static class AccessControlAllowMethods {
        public static final String httpName = "Access-Control-Allow-Methods";
        public final ArrayList<Method> allowedMethods;

        public AccessControlAllowMethods(ArrayList<Method> allowedMethods) {
            this.allowedMethods = allowedMethods;
        }
    }

    public static class AccessControlAllowHeaders {
        public static final String httpName = "Access-Control-Allow-Headers";
        public final ArrayList<String> allowedHeaders;

        public AccessControlAllowHeaders(ArrayList<String> allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }
    }

    public static class AccessControlRequestMethod {
        public static final String httpName = "Access-Control-Request-Method";

        public final Method method;

        public AccessControlRequestMethod(Method method) {
            this.method = method;
        }
    }

    public static class AccessControlRequestHeaders {
        public static final String httpName = "Access-Control-Request-Headers";

        public final ArrayList<String> requestHeaders;

        public AccessControlRequestHeaders(ArrayList<String> requestHeaders) {
            this.requestHeaders = requestHeaders;
        }
    }

    //URI headers
    // Content-Location, Host, Location, Referer, Via, Origin, AccessControlAllowOrigin
    public static class URI {

    }
}
