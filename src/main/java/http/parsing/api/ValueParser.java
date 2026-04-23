package http.parsing.api;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;


public interface ValueParser<H> {
    H decode(RequestByteStream rbs, Buffer bfr);
    void encode(ResponseByteStream rbs, H header);
}
