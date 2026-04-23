package http.parsing.headers;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class DateParserEncoder implements ValueParser<Date> {
    @Override
    public Date decode(RequestByteStream bs, Buffer bfr) {
        return new Date(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public void encode(ResponseByteStream rbs, Date header) {
        BaseEncoder.IMF_FIX_DATE(rbs, header.value);
    }
}
