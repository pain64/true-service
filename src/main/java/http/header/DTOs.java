package http.header;

import http.Base;

import java.util.ArrayList;

import static http.HttpParser.*;

public class DTOs {
    public sealed interface MediaRangeType {
        record StarStar() implements MediaRangeType { }
        record TokenStar(String type) implements MediaRangeType { }
        record TokenToken(String type, String subtype) implements MediaRangeType { }
    }

    public class MediaRange extends HeaderLine {
        public final MediaRangeType mediaRange;
        public final ArrayList<Parameter> parameters;
        public final Float weight;
    }

    public static class Accept extends Header {
        public final ArrayList<MediaRange> value;

        public Accept(ArrayList<MediaRange> value) {
            this.value = value;
        }
    }

    public sealed interface Charset {
        record Star() implements Charset { }
        record Token(String type) implements Charset { }
    }

    public record CharsetWithWeight (Charset charset, Float weight) {}

    public static class AcceptCharset extends Header {
        public final ArrayList<CharsetWithWeight> value;

        public AcceptCharset(ArrayList<CharsetWithWeight> value) {
            this.value = value;
        }
    }

    public sealed interface Encoding {
        record Star() implements Encoding { }
        record Identity() implements Encoding { }
        record Token(String type) implements Encoding { }
    }

    public record EncodingWithWeight (Encoding encoding, Float weight) {}

    public static class AcceptEncoding extends Header {
        public final ArrayList<EncodingWithWeight> value;

        public AcceptEncoding(ArrayList<EncodingWithWeight> value) {
            this.value = value;
        }
    }


    public sealed interface LanguageRange {
        record Star() implements LanguageRange { }
        record One(String value) implements LanguageRange { }
        record Range(String rangeStart, String rangeEnd) implements LanguageRange { }
    }

    public record LanguageRangeWithWeight (LanguageRange range, Float weight) {}

    public static class AcceptLanguage extends Header {
        public final ArrayList<LanguageRangeWithWeight> value;

        public AcceptLanguage(ArrayList<LanguageRangeWithWeight> value) {
            this.value = value;
        }
    }

    public sealed interface AcceptRangeType {
        record None() implements AcceptRangeType { }
        record Bytes() implements AcceptRangeType { }
        record Token(String value) implements AcceptRangeType { }
    }

    public static class AcceptRanges extends Header {
        public final ArrayList<AcceptRangeType> value;

        public AcceptRanges(ArrayList<AcceptRangeType> value) {
            this.value = value;
        }
    }

    public sealed interface Method {
        record Get() implements Method { }
        record Head() implements Method { }
        record Post() implements Method { }
        record Put() implements Method { }
        record Delete() implements Method { }
        record Connect() implements Method { }
        record Options() implements Method { }
        record Trace() implements Method { }
        record Patch() implements Method { }
        record Token(String value) implements Method { }
    }

    public static class Allow extends Header {
        public final ArrayList<Method> value;

        public Allow(ArrayList<Method> value) {
            this.value = value;
        }
    }

    public sealed interface AuthParam {
        record Token(String name, String value) implements AuthParam {}
    }

    public static class AuthenticationInfo extends Header {
        public final ArrayList<AuthParam> value;

        public AuthenticationInfo(ArrayList<AuthParam> value) {
            this.value = value;
        }
    }

    public static class Authorization extends Header {
        public final String authSchema;
        public final String token;
        public final ArrayList<AuthParam> authPararms;

        public Authorization(String authSchema, String token, ArrayList<AuthParam> authPararms) {
            this.authSchema = authSchema;
            this.token = token;
            this.authPararms = authPararms;
        }
    }

    public static class Connection extends Header {
        public final ArrayList<String> value;

        public Connection(ArrayList<String> value) {
            this.value = value;
        }
    }

    public static class ContentEncoding extends Header {
        public final ArrayList<String> value;

        public ContentEncoding(ArrayList<String> value) {
            this.value = value;
        }
    }

    public static class ContentLength extends Header {
        public final long value;

        public ContentLength(long value) {
            this.value = value;
        }
    }

    public sealed interface RangeUnit {
        record Bytes() implements RangeUnit { }
        record Token(String value) implements RangeUnit { }
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
        record Star() implements ContentRangeType { }
        record Value(long value) implements ContentRangeType { }
        record Interval(long from, long to) implements ContentRangeType { }
    }

    public static class ContentRange extends Header {
        public final RangeUnit rangeUnit;
        public final ContentRangeType range;
        public final ContentRangeType size;

        public ContentRange(RangeUnit rangeUnit, ContentRangeType range, ContentRangeType size) {
            this.rangeUnit = rangeUnit;
            this.range = range;
            this.size = size;
        }
    }

    public static class Parameter {
        public final String name;
        public final String value;

        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class ContentType extends Header {
        public final String type;
        public final String subtype;
        public final ArrayList<Parameter> value;

        public ContentType(String type, String subtype, ArrayList<Parameter> value) {
            this.type = type;
            this.subtype = subtype;
            this.value = value;
        }
    }

    public static class Date extends Header {
        public final String value;

        public Date(String value) {
            this.value = value;
        }
    }

    public sealed interface EntityTag {
        public record Default(String value) implements EntityTag {}
        public record Weak(String value) implements EntityTag {}
    }

    public sealed interface IfRangeType {
        public record EntityTag(DTOs.EntityTag value) implements IfRangeType {}
        public record Date(String value) implements IfRangeType {}
    }

    public static class ETag extends Header {
        public final EntityTag value;

        public ETag(EntityTag value) {
            this.value = value;
        }
    }

    public sealed interface MatchEntitiesTags {
        public record All() implements MatchEntitiesTags {}
        public record EntitiesTags(ArrayList<EntityTag> value) implements MatchEntitiesTags {}
    }

    public static class IfMatch extends Header {
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

    public static class Expect extends Header {
        public final ArrayList<Expectation> value;

        public Expect(ArrayList<Expectation> value) {
            this.value = value;
        }
    }

    public static class IfModifiedSince extends Date {

        public IfModifiedSince(String date) {
            super(date);
        }
    }

    public static class IfNoneMatch extends Header {
        public final MatchEntitiesTags value;

        public IfNoneMatch(MatchEntitiesTags value) {
            this.value = value;
        }
    }

    public static class IfRange extends Header {
        public final IfRangeType value;

        public IfRange(IfRangeType value) {
            this.value = value;
        }
    }

    public static class IfUnmodifiedSince extends Date {

        public IfUnmodifiedSince(String date) {
            super(date);
        }
    }

    public static class LastModified extends Date {

        public LastModified(String date) {
            super(date);
        }
    }

    public static class MaxForwards extends Header {
        public final long value;

        public MaxForwards(long value) {
            this.value = value;
        }
    }

    public static class Challenge {
        public final String authSchema;
        public final String token;
        public final ArrayList<AuthParam> authPararms;

        public Challenge(String authSchema, String token, ArrayList<AuthParam> authPararms) {
            this.authSchema = authSchema;
            this.token = token;
            this.authPararms = authPararms;
        }
    }

    public static class Authenticate extends Header {
        public final ArrayList<Challenge> value;

        public Authenticate(ArrayList<Challenge> value) {
            this.value = value;
        }
    }

    public static class ProxyAuthenticate extends Authenticate {
        public ProxyAuthenticate(ArrayList<Challenge> value) {
            super(value);
        }
    }

    public static class ProxyAuthenticationInfo extends AuthenticationInfo {
        public final ArrayList<AuthParam> value;

        public ProxyAuthenticationInfo(ArrayList<AuthParam> value) {
            super(this.value = value);
        }
    }

    public static class ProxyAuthorization extends Authorization {
        public ProxyAuthorization(Authorization auth) {
            super(auth.authSchema, auth.token, auth.authPararms);
        }
    }

    public sealed interface RangeSpec  {
        record Start(long value) implements RangeSpec {}
        record Interval(long from, long to) implements RangeSpec {}
        record Suffix(long value) implements RangeSpec {}
    }

    public static class Range extends Header {
        public final RangeUnit rangeUnit;
        public final ArrayList<RangeSpec> value;

        public Range(RangeUnit rangeUnit, ArrayList<RangeSpec> value) {
            this.rangeUnit = rangeUnit;
            this.value = value;
        }
    }

    public sealed interface RetryAfterType {
        record HttpDate(String value) implements RetryAfterType{}
        record DelaySeconds(long value) implements RetryAfterType {}
    }
    public static class RetryAfter extends Header {
        public final RetryAfterType value;

        public RetryAfter(RetryAfterType value) {
            this.value = value;
        }
    }

    public static class Server extends Header {
        public final ArrayList<Product> value;

        public Server(ArrayList<Product> value) {
            this.value = value;
        }
    }

    public sealed interface TCoding {
        record Trailers() implements TCoding {}
        record Value(String transferCoding, ArrayList<Parameter> parameters, Float weight) implements TCoding {}
    }
    public static class TE extends Header {
        public final ArrayList<TCoding> value;

        public TE(ArrayList<TCoding> value) {
            this.value = value;
        }
    }

    public static class Trailer extends Header {
        public final ArrayList<String> value;

        public Trailer(ArrayList<String> value) {
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
    public static class Upgrade extends Header {
        public final ArrayList<Protocol> value;

        public Upgrade(ArrayList<Protocol> value) {
            this.value = value;
        }
    }

    public static class UserAgent extends Header {
        public final ArrayList<Product> value;

        public UserAgent(ArrayList<Product> value) {
            this.value = value;
        }
    }

    public sealed interface VaryType {
        record Empty() implements VaryType {}
        record Star() implements VaryType {}
        record Fields(ArrayList<String> value) implements VaryType {}
    }
    public static class Vary extends Header {
        public final VaryType value;

        public Vary(VaryType value) {
            this.value = value;
        }
    }

    public static class  WWWAuthenticate extends Authenticate {
        public WWWAuthenticate(ArrayList<Challenge> value) {
            super(value);
        }
    }

}
