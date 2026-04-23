package http.parsing.headers.Accept;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;
import http.parsing.api.ValueParser;

import static http.parsing.BaseDecoder.*;
import static http.dto.Headers.*;

public class AcceptRangesParserEncoder implements ValueParser<AcceptRanges> {
    @Override
    public AcceptRanges decode(RequestByteStream bs, Buffer bfr) {
        CHAR(bs, 'b'); CHAR(bs, 'y');CHAR(bs, 't');CHAR(bs, 'e');CHAR(bs, 's');
        return new AcceptRanges();
    }

    @Override
    public void encode(ResponseByteStream rbs, AcceptRanges header) {
        rbs.push("bytes");
    }
}
