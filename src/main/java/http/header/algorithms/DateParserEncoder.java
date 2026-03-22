package http.header.algorithms;

import http.BaseEncoder;

import static http.BaseDecoder.*;
import static http.HttpParser.*;
import static http.header.DTOs.*;

public class DateParserEncoder implements HeaderParser<Date> {
    @Override
    public Date decode(ByteStream bs, Buffer bfr) {
        return new Date(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public void encode(BaseEncoder.ResponseByteStream rbs, Date header) {
        BaseEncoder.IMF_FIX_DATE(rbs, header.value);
    }
}
