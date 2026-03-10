package http;

public class BaseEncoder {

    public interface StatusLineEncoder {
        void setHttpVersion(String version);
        void setStatusCode(String statusCode);
        void setReasonPhrase(String value);
        void endStatusLine();
    }

    public interface HeaderEncoder {
        void startHeader(String version);
        void setHeaderName(String version);
        void endHeader(String statusCode);
    }

    public interface BodyEncoder {
        void startHeader(String version);
        void setHeaderName(String version);
        void endHeader(String statusCode);
    }

    public class ResponseByteStream {
        private String headerName = null;
        private boolean isMultilineHeader = false;
        public final int MAX_HEADER_LENGTH = 1024;
        private int CURRENT_HEADER_LENGTH = 0;

        public void forward(char b) {

        }

        public void push(char b) {

        }

        public void push(String v) {

        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public void setMultiline(boolean isMultilineHeader) {
            this.isMultilineHeader = isMultilineHeader;
        }

        public void reset() {
            headerName = null;
            isMultilineHeader = false;
        }

    }

}
