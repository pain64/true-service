package http.header.algorithms.Cookie;

import static http.HttpParser.*;

public class DTO {
    abstract public static class Cookie implements Header { }

    public static class SetCookieBuilder<K, V> {
        private final Class<? extends SetCookie<K, V>> headerClass;
        private final K name;
        private final V value;

        private String expires;
        private Long maxAge;
        private String domain;
        private String path;
        private Boolean secure;
        private Boolean httpOnly;
        private String extension;

        public SetCookieBuilder(Class<? extends SetCookie<K, V>> header, K name, V value) {
            this.headerClass = header;
            this.name = name;
            this.value = value;
        }

        public SetCookieBuilder<K, V> setExpires(String expires) {
            this.expires = expires;
            return this;
        }

        public SetCookieBuilder<K, V> setMaxAge(long maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public SetCookieBuilder<K, V> setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public SetCookieBuilder<K, V> setPath(String path) {
            this.path = path;
            return this;
        }

        public SetCookieBuilder<K, V> setSecure() {
            this.secure = true;
            return this;
        }

        public SetCookieBuilder<K, V> setHttpOnly() {
            this.httpOnly = true;
            return this;
        }

        public SetCookieBuilder<K, V> setExtension(String extension) {
            this.extension = extension;
            return this;
        }

        public SetCookie<K, V> build() {
            try {
                return (headerClass.cast(headerClass.getConstructors()[0].newInstance(name, value, expires, maxAge, domain, path, secure, httpOnly, extension)));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    abstract public static class SetCookie<K, V> implements Header {
        public final K name;
        public final V value;

        public final String expires;
        public final Long maxAge;
        public final String domain;
        public final String path;
        public final Boolean secure;
        public final Boolean httpOnly;
        public final String extension;

        public SetCookie(K name, V value, String expires, Long maxAge, String domain, String path, Boolean secure, Boolean httpOnly, String extension) {
            this.name = name;
            this.value = value;
            this.expires = expires;
            this.maxAge = maxAge;
            this.domain = domain;
            this.path = path;
            this.secure = secure;
            this.httpOnly = httpOnly;
            this.extension = extension;
        }
    }

    public static class SessionID extends SetCookie<String, String> {
        public SessionID(String name, String value, String expires, Long maxAge, String domain, String path, Boolean secure, Boolean httpOnly, String extension) {
            super(name, value, expires, maxAge, domain, path, secure, httpOnly, extension);
        }
    }

    public SetCookie<String, String> createSessionIdSetCookie(String sessionId) {
        return
            new SetCookieBuilder<String, String>(SessionID.class, "sessionID", sessionId)
                .setMaxAge(1200)
                .build();
    }
}
