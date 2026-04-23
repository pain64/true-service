package http.parsing.api;

import http.RequestByteStream;

public class ParseException {

    public static class DecodeException extends RuntimeException {
        final long rbsPosition;

        public DecodeException(RequestByteStream rbs, String message) {
            this.rbsPosition = rbs.requestPosition();
            super(message);
        }

        public DecodeException(RequestByteStream rbs, Exception e) {
            this.rbsPosition = rbs.requestPosition();
            super(e);
        }
    }

    public static class EncodeException extends RuntimeException {
        public EncodeException(String message) {
            super(message);
        }
    }

}
