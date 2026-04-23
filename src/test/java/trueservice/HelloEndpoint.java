package trueservice;


import http.Buffer;
import http.RequestByteStream;
import http.parsing.headers.Accept.AcceptParserEncoder;
import http.parsing.headers.Auth.AuthenticationInfoParserEncoder;
import http.parsing.headers.Auth.AuthorizationParserEncoder;
import http.parsing.headers.Content.ContentTypeParserEncoder;
import http.parsing.headers.DateParserEncoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static http.dto.Headers.*;
import static http.parsing.BaseDecoder.*;



public class HelloEndpoint {
    private static final String method = "GET";
    private static final String path = "hello\\";

    private static final int MAX_BUFFER_SIZE = 2048;

    private static final ContentTypeParserEncoder contentTypeParserEncoder = new ContentTypeParserEncoder();
    private static final AuthorizationParserEncoder authorizationParserEncoder = new AuthorizationParserEncoder();
    private static final DateParserEncoder dateParserEncoder = new DateParserEncoder();
    private static final AcceptParserEncoder acceptParserEncoder = new AcceptParserEncoder();
    private static final AuthenticationInfoParserEncoder authenticationInfoParserEncoder = new AuthenticationInfoParserEncoder();

    private static int searchQueryParam(RequestByteStream rbs) {
        var i = 0;
        switch ((rbs.lookahead(0) & ((byte) 1 << 0)) >>> 0) {
            case 0:
                for (var b: "name=".getBytes(StandardCharsets.UTF_8)) {
                    if (rbs.lookahead(i++) != b) {
                        return -1;
                    }
                }
                return 1 ;
            case 1:
                for (var b: "id=".getBytes(StandardCharsets.UTF_8)) {
                    if (rbs.lookahead(i++) != b) {
                        return -1;
                    }
                }
                return 2 ;
        }
        return -1;
    }

    private static int searchHeader(RequestByteStream rbs) {
        var i = 0;
        switch ((rbs.lookahead(1) & ((byte) 3 << 1)) >>> 1) {
            case 0:
                for (var b: "Date:".getBytes(StandardCharsets.UTF_8)) {
                    if (rbs.lookahead(i++) != b) {
                        return -1;
                    }
                }
                return 3 ;
            case 1:
                for (var b: "Accept:".getBytes(StandardCharsets.UTF_8)) {
                    if (rbs.lookahead(i++) != b) {
                        return -1;
                    }
                }
                return 4 ;
            case 2:
                switch ((rbs.lookahead(4) & ((byte) 1 << 1)) >>> 1) {
                    case 0:
                        for (var b: "Authentication-Info:".getBytes(StandardCharsets.UTF_8)) {
                            if (rbs.lookahead(i++) != b) {
                                return -1;
                            }
                        }
                        return 5 ;
                    case 1:
                        for (var b: "Authorization:".getBytes(StandardCharsets.UTF_8)) {
                            if (rbs.lookahead(i++) != b) {
                                return -1;
                            }
                        }
                        return 2 ;
                }
            case 3:
                switch ((rbs.lookahead(2) & ((byte) 1 << 0)) >>> 0) {
                    case 0:
                        for (var b: "Content-Type:".getBytes(StandardCharsets.UTF_8)) {
                            if (rbs.lookahead(i++) != b) {
                                return -1;
                            }
                        }
                        return 1 ;
                    case 1:
                        for (var b: "Cookie:".getBytes(StandardCharsets.UTF_8)) {
                            if (rbs.lookahead(i++) != b) {
                                return -1;
                            }
                        }
                        return 111222 ;
                }
        }
        return -1;
    }

    private static int searchCookie(RequestByteStream rbs) {
        var i = 0;
        switch ((rbs.lookahead(0) & ((byte) 1 << 0)) >>> 0) {
            case 0:
                for (var b: "JSESSIONID=".getBytes(StandardCharsets.UTF_8)) {
                    if (rbs.lookahead(i++) != b) {
                        return -1;
                    }
                }
                return 1 ;
            case 1:
                for (var b: "SECRET=".getBytes(StandardCharsets.UTF_8)) {
                    if (rbs.lookahead(i++) != b) {
                        return -1;
                    }
                }
                return 2 ;
        }
        return -1;
    }

    public void invoke(RequestByteStream rbs) {

        var bfr = new Buffer(MAX_BUFFER_SIZE);

        rbs.movePosition(method.length() + path.length() + 1);
        String pathName = null;
        Integer pathId = null;

        TOKEN_PERCENT_ENCODED(rbs, bfr);
        pathName = bfr.toStringAndReset();
        CHAR(rbs, '\\');
        pathId = (int) UNSIGNED_LONG(rbs);

        String queryName = null;
        Integer queryId = null;

        CHAR(rbs, '?');
        do {
            switch (searchQueryParam(rbs)) {
                case 1:
                    TOKEN_PERCENT_ENCODED(rbs, bfr);
                    queryName = bfr.toStringAndReset();
                    break;
                case 2:
                    queryId = (int) UNSIGNED_LONG(rbs);
                    break;
                default:
            }
        } while (AMPERSAND_SKIP(rbs));

        if (
            queryId == null
        ) {
            ArrayList<String> queryParamsNotFound = new ArrayList<>();
            if (queryId == null) queryParamsNotFound.add("id");
            // response 400 with info;
        }

        CHAR(rbs, ' ');
        CHAR(rbs, 'H'); CHAR(rbs, 'T'); CHAR(rbs, 'T'); CHAR(rbs, 'P'); CHAR(rbs, '/');
        int httpMajorDigit = NDIGIT(rbs, 1);
        CHAR(rbs, '.');
        int httpMinorDigit = NDIGIT(rbs, 1);

        CRLF(rbs);

        ContentType contentType = null;
        Authorization auth = null;
        Date date = null;
        Accept accept = null;
        ArrayList<MediaRange> acceptValue = new ArrayList<>();
        AuthenticationInfo authInfo = null;
        ArrayList<AuthParam> authInfoValue = new ArrayList<>();
        String JSessionId = null;
        Integer secret = null;

        while (!IS_CHAR(rbs, '\r')) {
            switch (searchHeader(rbs)) {
                case 1:
                    contentType = contentTypeParserEncoder.decode(rbs, bfr);
                    break;
                case 2:
                    auth = authorizationParserEncoder.decode(rbs, bfr);
                    break;
                case 3:
                    date = dateParserEncoder.decode(rbs, bfr);
                    break;
                case 4:
                    acceptParserEncoder.decode(rbs, bfr, acceptValue);
                    break;
                case 5:
                    authenticationInfoParserEncoder.decode(rbs, bfr, authInfoValue);
                    break;
                case 111222:
                    do {
                        switch (searchCookie(rbs)) {
                            case 1:
                                TOKEN_COOKIE(rbs, bfr);
                                JSessionId = bfr.toStringAndReset();
                                break;
                            case 2:
                                secret = (int) UNSIGNED_LONG(rbs);
                                break;
                            default:
                        }
                    } while (OWS_DELIMITER_OWS_SKIP(rbs, ';'));
                    break;
                default:
            }
            SKIP_TO_CRLF(rbs);
        }

        if (
            auth == null
                || secret == null
        ) {
            ArrayList<String> headersNotFound = new ArrayList<>();
            if (auth == null) headersNotFound.add("Authorization");
            ArrayList<String> cookieNotFound = new ArrayList<>();
            if (secret == null) cookieNotFound.add("SECRET");
            // response 400 with info;
        }

        accept = new Accept(acceptValue);
        authInfo = new AuthenticationInfo(authInfoValue);

        CRLF(rbs);
    }
}