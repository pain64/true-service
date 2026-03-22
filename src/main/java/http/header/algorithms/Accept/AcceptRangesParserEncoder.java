package http.header.algorithms.Accept;

import http.BaseEncoder;
import http.BaseEncoder.ResponseByteStream;
import http.HttpParser;
import http.HttpParser.ValueListHeaderParser;

import java.util.ArrayList;

import static http.BaseDecoder.*;
import static http.header.DTOs.*;

public class AcceptRangesParserEncoder implements HttpParser.HeaderParser<AcceptRanges> {
    @Override
    public AcceptRanges decode(ByteStream bs, Buffer bfr) {
        CHAR(bs, 'b'); CHAR(bs, 'y');CHAR(bs, 't');CHAR(bs, 'e');CHAR(bs, 's');
        return new AcceptRanges();
    }

    @Override
    public void encode(ResponseByteStream rbs, AcceptRanges header) {
        rbs.push("bytes");
    }
}
