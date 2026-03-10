package http.header.algorithms;

import static http.BaseParser.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class ProxyAuthenticateParserEncoder implements HeaderParser<ProxyAuthenticate>, HeaderEncoder<ProxyAuthenticate> {
    @Override
    public ProxyAuthenticate PARSE_HEADER(ByteStream bs, Buffer bfr) {
        //challenge list
        return null;
    }

    @Override
    public void ENCODE_HEADER(ResponseByteStream rbs, ProxyAuthenticate header) {
        return new byte[0];
    }

}
