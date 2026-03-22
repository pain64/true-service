package http.header.algorithms;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class LastModifiedParserEncoder implements HeaderParser<LastModified> {
    @Override
    public LastModified decode(ByteStream bs, Buffer bfr) {
        return new LastModified(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public void encode(ResponseByteStream rbs, LastModified header) {
        BaseEncoder.IMF_FIX_DATE(rbs, header.value);
    }
}
