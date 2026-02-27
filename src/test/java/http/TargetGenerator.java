package http;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static http.AllowedRequestTargetGenerator.allowedRequestTargetNavigator;

public class TargetGenerator {
    @Test
    void test() throws Exception {
        var arrL = new ArrayList<String>();
        arrL.add("/");
        arrL.add("/hello");
        arrL.add("/hello/");
        arrL.add("/he");
        arrL.add("/he/");
        arrL.add("/privet/");
        arrL.add("/");

        allowedRequestTargetNavigator(arrL);
    }
}
