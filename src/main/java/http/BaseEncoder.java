package http;

import http.header.DTOs;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import static http.HttpParser.*;
import static http.header.DTOs.*;

public class BaseEncoder {

    public class ResponseByteStream {


        public void push(char ch) {

        }

        public void push(String v) {

        }

    }

    public static void WEIGHT(ResponseByteStream rbs, Float weight) {
        if (weight == null) return;

        rbs.push(';'); rbs.push('q'); rbs.push('=');

        var v = Math.round(weight * 1000);
        rbs.push((char)  ((v / 1000) % 10 + (byte) '0'));
        rbs.push('.');
        rbs.push((char) ((v / 100) % 10 + (byte) '0'));
        rbs.push((char) ((v / 10) % 10 + (byte) '0'));
        rbs.push((char) (v % 10 + (byte) '0'));
    }

    public static void PARAMETERS(ResponseByteStream rbs, ArrayList<Parameter> parameters) {
        for (Parameter parameter : parameters) {
            var name = parameter.name;
            var value = parameter.value;
            rbs.push(';');
            rbs.push(name);
            rbs.push('=');
            rbs.push(value);
        }
    }

    public static void METHOD(ResponseByteStream rbs, Method method) {
        String value = switch (method) {
            case Method.Get _ -> "GET";
            case Method.Head _ -> "HEAD";
            case Method.Post _ -> "POST";
            case Method.Put _ -> "PUT";
            case Method.Delete _ -> "DELETE";
            case Method.Connect _ -> "CONNECT";
            case Method.Options _ -> "OPTIONS";
            case Method.Trace _ -> "TRACE";
            case Method.Patch _ -> "PATCH";
            default -> ((Method.Token) method).value;
        };

        rbs.push(value);
    }

    public static void METHODS(ResponseByteStream rbs, ArrayList<Method> methods) {
        for (var i = 0; i < methods.size(); i++) {
            BaseEncoder.METHOD(rbs, methods.get(i));
            if (methods.size()-1 != i) rbs.push(',');
        }
    }

    public static void AUTH_PARAMS(ResponseByteStream rbs, ArrayList<AuthParam> authParams) {
        for (var i = 0; i < authParams.size(); i++) {
            rbs.push(authParams.get(i).name); rbs.push('=');
            rbs.push('"'); rbs.push(authParams.get(i).value); rbs.push('"');

            if (authParams.size()-1 != i) rbs.push(',');
        }
    }

    public static void AUTHENTICATION_INFO(ResponseByteStream rbs, AuthenticationInfo header) {
        AUTH_PARAMS(rbs, header.value);
    }

    public static void AUTHORIZATION(ResponseByteStream rbs, Authorization header) {
        rbs.push(header.authSchema);

        if (header.token != null || !header.authParams.isEmpty()) {
            rbs.push(' ');
            if (header.token != null) rbs.push(header.token);
            else AUTH_PARAMS(rbs, header.authParams);
        }
    }

    public static void AUTHENTICATE(ResponseByteStream rbs, Authenticate header) {
        for (var i = 0; i < header.value.size(); i++) {
            AUTHORIZATION(rbs, header.value.get(i));
            if (header.value.size()-1 != i) rbs.push(',');
        }
    }

    public static void ENTITY_TAG(ResponseByteStream rbs, EntityTag entityTag) {
        if (entityTag instanceof EntityTag.Weak) {
            rbs.push("W\\");
            rbs.push(((EntityTag.Weak) entityTag).value);
        } else {
            rbs.push(((EntityTag.Default) entityTag).value);
        }
    }

    public static void IF_MATCH(ResponseByteStream rbs, IfMatch header) {
        if (header.value instanceof MatchEntitiesTags.All) {
            rbs.push('*');
        } else {
            var tags = ((MatchEntitiesTags.EntitiesTags) header.value).value;
            for (var i = 0; i < tags.size(); i++) {
                ENTITY_TAG(rbs, tags.get(i));
                if (tags.size()-1 != i) rbs.push(',');
            }
        }
    }

    public static final DateTimeFormatter HTTP_DATE_FORMATTER = DateTimeFormatter
        .ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)
        .withZone(ZoneOffset.UTC);

    public static void IMF_FIX_DATE(ResponseByteStream rbs, LocalDateTime value) {
        rbs.push(value.format(HTTP_DATE_FORMATTER));
    }

    public static void TOKENS_COMMA_SEPARATED(ResponseByteStream rbs, ArrayList<String> tokens) {
        for (var i = 0; i < tokens.size(); i++) {
            rbs.push(tokens.get(i));
            if (tokens.size()-1 != i) rbs.push(',');
        }
    }

    public static void NUMBER(ResponseByteStream rbs, long value) {
        var i = 1;
        var a = value;
        // 523 534 535
        do {
            i *= 10;
        } while ((a = a / 10) != 0);
        // todo check for max value

        while (i != 1) {
            var b = value / i;
            rbs.push((char) (b + '0'));

            value = value - b * i;
            i /= 10;
        }
    }

    public static void RANGE_UNIT(ResponseByteStream rbs, RangeUnit rangeUnit) {
        if (rangeUnit instanceof RangeUnit.Bytes) rbs.push("bytes");
        else rbs.push(((RangeUnit.Token) rangeUnit).value);
    }

    public static void COMMENT(ResponseByteStream rbs, String comment) {
        rbs.push('(');
        rbs.push(comment);
        rbs.push(')');
    }

    public static void PRODUCTS(ResponseByteStream rbs, ArrayList<Product> products) {
        for (var i = 0; i < products.size(); i++) {
            var product = products.get(i);

            rbs.push(product.name);
            if (product.version != null) {
                rbs.push('/'); rbs.push(product.version);
            }
            if (product.comment != null) {
                rbs.push(' ');
                COMMENT(rbs, product.comment);
            }
            if (i != products.size()-1) rbs.push(' ');
        }
    }

}
