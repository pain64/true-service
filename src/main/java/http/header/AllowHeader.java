package http.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.Buffer;
import static http.Base.ByteStream;
import static http.HttpParser.*;
import static http.JumpTables.IS_TCHAR_TABLE;

public class AllowHeader implements HeaderParser<AllowHeader.Allow>, HeaderEncoder<AllowHeader.Allow> {

    public static class Allow extends Header {
        public final ArrayList<Method> value;

        public Allow(ArrayList<Method> value) {
            this.value = value;
        }
    }

    @Override
    public Allow PARSE_HEADER(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<Method>();

        while (TCHAR_CHECK(bs)) {
            TOKEN(bs, bfr, IS_TCHAR_TABLE, -1);
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

            OWS_SYMBOL_OWS_SKIP(bs, ',');
        }

        return new Allow(value);
    }

    @Override
    public byte[] ENCODE_HEADER(Allow header) {
        return new byte[0];
    }
}
