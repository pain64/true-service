package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class LastModifiedParserEncoder implements HeaderParser<LastModified>, HeaderEncoder<LastModified> {
    @Override
    public LastModified PARSE_HEADER(ByteStream bs, Buffer bfr) {
        return new LastModified(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, LastModified header) {
        return new byte[0];
    }

}
