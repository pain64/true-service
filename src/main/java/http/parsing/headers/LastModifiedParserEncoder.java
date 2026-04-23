package http.parsing.headers;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.BaseEncoder;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class LastModifiedParserEncoder implements ValueParser<LastModified> {
    @Override
    public LastModified decode(RequestByteStream bs, Buffer bfr) {
        return new LastModified(IMF_FIX_DATE(bs, bfr));
    }

    @Override
    public void encode(ResponseByteStream rbs, LastModified header) {
        BaseEncoder.IMF_FIX_DATE(rbs, header.value);
    }
}
