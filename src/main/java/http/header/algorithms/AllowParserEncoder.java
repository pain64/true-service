package http.header.algorithms;

import java.util.ArrayList;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class AllowParserEncoder implements HeaderParserMultiline<Method>, HeaderEncoder<Allow> {
    @Override
    public void PARSE_HEADER(ByteStream bs, Buffer bfr, ArrayList<Method> toAdd) {
        if (!IS_TCHAR(bs)) return;

        do {
            TOKEN_TCHAR(bs, bfr);
            var token = bfr.toStringAndReset();

            toAdd.add(
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
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, Allow header) {
        return new byte[0];
    }

}
