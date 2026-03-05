package http.header;

import http.Base;
import java.util.ArrayList;

import static http.Base.TOKENS_COMMA_SEPARATED;
import static http.HttpParser.*;
import static http.header.TrailerHeader.*;

public class TrailerHeader implements HeaderParser<Trailer>, HeaderEncoder<Trailer> {
    public static class Trailer extends Header {
        public final ArrayList<String> value;

        public Trailer(ArrayList<String> value) {
            this.value = value;
        }
    }

    @Override
    public Trailer PARSE_HEADER(Base.ByteStream bs, Base.Buffer bfr) {
        return new Trailer(TOKENS_COMMA_SEPARATED(bs, bfr));
    }

    @Override
    public byte[] ENCODE_HEADER(Trailer header) {
        return new byte[0];
    }
}
