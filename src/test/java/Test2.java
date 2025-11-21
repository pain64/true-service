
import org.example.shortener.UrlShortenerApi;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.lang.classfile.attribute.RuntimeInvisibleAnnotationsAttribute;
import java.lang.classfile.attribute.RuntimeInvisibleParameterAnnotationsAttribute;
import java.lang.classfile.attribute.RuntimeInvisibleTypeAnnotationsAttribute;

public class Test2 {
    @Test public void test() throws IOException {
        try (var res = getClass().getResourceAsStream(UrlShortenerApi.class.getName().replace(".", "/") + ".class")) {
            var cm = ClassFile.of().parse(
                res.readAllBytes()
            );
            var m = cm.methods().get(3);

            for (var attr : m.attributes()) {
                if (attr instanceof RuntimeInvisibleParameterAnnotationsAttribute x) {
                    // parameter
                    var xx = 1;
                }
                if (attr instanceof RuntimeInvisibleTypeAnnotationsAttribute x) {
                    // type parameters of parameter
                    var xx = 1;
                }
                if (attr instanceof RuntimeInvisibleAnnotationsAttribute x) {
                    // return value
                    var xx = 1;
                }
            }

            var xx = 1;
        }

    }
}
