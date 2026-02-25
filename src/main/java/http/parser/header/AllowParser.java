package http.parser.header;

import java.util.ArrayList;

import static http.Base.*;
import static http.Base.BYTE_OPT;
import static http.Base.Buffer;
import static http.Base.ByteStream;
import static http.Base.SKIP_OWS;

public class AllowParser {

    public static class Allow {
        public final ArrayList<Method> value;

        public Allow(ArrayList<Method> value) {
            this.value = value;
        }
    }

    public static Allow ALLOW(ByteStream bs, Buffer bfr) {
        var value = new ArrayList<Method>();
        bfr.reset();

        byte b;
        if ((b = TCHAR_OPT(bs)) == -1) return null;
        bs.unadvance(b);

        while (true) {
            TOKEN_OPT(bs, bfr);
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

            SKIP_OWS(bs);
            if (BYTE_OPT(bs, ',') != -1) break;
            SKIP_OWS(bs);
        }

        return new Allow(value);
    }
}
