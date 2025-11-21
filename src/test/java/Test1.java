import net.truej.service.Exchange;
import net.truej.service.xxx.Interceptor;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

public class Test1 {

    static class EE implements Exchange { }

    @Test void test() {
        new String(new byte[]{1}, StandardCharsets.UTF_8);
//        var i1 = (Interceptor<EE>) (xc, invocation) -> {
//            System.out.println("xc");
//            System.out.println("i1");
//            invocation.run();
//        };
//        var i2 = (Interceptor<EE>) (xc, invocation) -> {
//            System.out.println("xc");
//            System.out.println("i2");
//            invocation.run();
//        };
//        var i3 = (Interceptor<EE>) (xc, invocation) -> {
//            System.out.println("xc");
//            System.out.println("i3");
//            invocation.run();
//        };
//
//        var overall = Server.joinInterceptors(
//            List.of(i1, i2, i3), 0, () -> System.out.println("method invoked")
//        );
//
//        overall.accept(new EE());
    }
}
