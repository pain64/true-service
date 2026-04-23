package http.parsing.headers.Cookie;


import http.Buffer;
import http.RequestByteStream;
import http.ResponseByteStream;

public class DTO {

    public interface CookieValue<T> {
        T decode(RequestByteStream bs, Buffer bfr);
        void encode(ResponseByteStream rbs);
    }

    public abstract static class CookiePart<V> implements CookieValue<V> {
        public final String name;
        public final V value;

        public CookiePart(String name, V value) {
            this.name = name;
            this.value = value;
        }
    }

    public abstract static class SetCookie<V> {
        public final CookiePart<V> cookie;

        public final String expires;
        public final Long maxAge;
        public final String domain;
        public final String path;
        public final Boolean secure;
        public final Boolean httpOnly;
        public final String extension;

        public SetCookie(CookiePart<V> cookie, String expires, Long maxAge, String domain, String path, Boolean secure, Boolean httpOnly, String extension) {
            this.cookie = cookie;
            this.expires = expires;
            this.maxAge = maxAge;
            this.domain = domain;
            this.path = path;
            this.secure = secure;
            this.httpOnly = httpOnly;
            this.extension = extension;
        }
    }

    public static class SetCookieBuilder<V> {
        private final Class<? extends SetCookie<V>> headerClass;
        private final V cookie;

        private String expires;
        private Long maxAge;
        private String domain;
        private String path;
        private Boolean secure;
        private Boolean httpOnly;
        private String extension;

        public SetCookieBuilder(Class<? extends SetCookie<V>> header, V cookie) {
            this.headerClass = header;
            this.cookie = cookie;
        }

        public SetCookieBuilder<V> setExpires(String expires) {
            this.expires = expires;
            return this;
        }

        public SetCookieBuilder<V> setMaxAge(long maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public SetCookieBuilder<V> setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public SetCookieBuilder<V> setPath(String path) {
            this.path = path;
            return this;
        }

        public SetCookieBuilder<V> setSecure() {
            this.secure = true;
            return this;
        }

        public SetCookieBuilder<V> setHttpOnly() {
            this.httpOnly = true;
            return this;
        }

        public SetCookieBuilder<V> setExtension(String extension) {
            this.extension = extension;
            return this;
        }

        public SetCookie<V> build() {
            try {
                return (headerClass.cast(headerClass.getConstructors()[0].newInstance(cookie, expires, maxAge, domain, path, secure, httpOnly, extension)));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
