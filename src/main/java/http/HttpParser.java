package http;

import http.BaseEncoder.ResponseByteStream;

import java.util.ArrayList;

import static http.BaseDecoder.*;

// 6. тесты декод

// 7. URL + декод + енкод

// 6. тесты енкод

// 8. Language header

// 9. организация кода, типы, названия переменных, валидация переменных, настройки доступа

// 10. общий пайплайн, имплементация ByteStream, Buffer и ResponseByteStream

// 11. куки

// 12. JSON парсер

public class HttpParser {

    public static class HeaderDecodeException extends RuntimeException {
        final long bsPosition;

        public HeaderDecodeException(long bsPosition, String message) {
            this.bsPosition = bsPosition;
            super(message);
        }
    }

    public static class HeaderEncodeException extends RuntimeException {
        public HeaderEncodeException(String message) {
            super(message);
        }
    }

    public interface Header { }

    public interface ValueListHeader<V> extends Header {
//        ArrayList<V> header();
    }

    public interface HeaderParser<H extends Header> {
         H decode(ByteStream bs, Buffer bfr);
         void encode(ResponseByteStream rbs, H header);
    }

    public interface ValueListHeaderParser<V, H extends ValueListHeader<V>> {
        H create(ArrayList<V> valueArray);
        void decode(ByteStream bs, Buffer bfr, ArrayList<V> dest);
        void encode(ResponseByteStream rbs, H header);
    }

}
