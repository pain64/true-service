package http.header.algorithms;

import java.util.ArrayList;

import static http.Base.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class AllowParserEncoder implements HeaderParser<Allow>, HeaderEncoder<Allow> {

    @Override
    public Allow PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<Method>();

        if (!IS_TCHAR(bs)) return new Allow(value);

        do {
            TOKEN_TCHAR(bs, bfr);
            var token = bfr.toStringAndReset();

            value.add(
                switch (token) {
                    case "GET" -> new Method.Get();
                    case "HEAD" -> new Method.Head();
                    case "POST" -> new Method.Post();
                    case "PUT" -> new Method.Put();
                    case "DELETE" -> new Method.Delete();
                    case "CONNECT" -> new Method.Connect();
                    case "OPTIONS" -> new Method.Options();
                    case "TRACE" -> new Method.Trace();
                    case "PATCH" -> new Method.Patch();
                    default -> new Method.Token(token);
                });
        } while (OWS_SYMBOL_OWS_SKIP(bs, ','));

        return new Allow(value);
    }

    @Override
    public byte[] ENCODE_HEADER(Allow header) {
        return new byte[0];
    }
}
