package http.parsing.api;

import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;

import java.util.ArrayList;

public interface ListHeaderParser<V, H> {
    H create(ArrayList<V> valueArray);
    void decode(RequestByteStream rbs, Buffer bfr, ArrayList<V> dest);
    void encode(ResponseByteStream rbs, H header);
}
