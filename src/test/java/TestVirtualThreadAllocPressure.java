import io.netty.util.concurrent.ThreadPerTaskExecutor;
import org.apache.tomcat.util.threads.VirtualThreadExecutor;

import java.util.concurrent.Executor;

public class TestVirtualThreadAllocPressure {
    static void main() throws InterruptedException {
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");

        while (true) {
            var t1 = System.currentTimeMillis();
            for (var i = 0; i < 1_000_000; i++)
                Thread.ofVirtual().start(() -> {

                }).join();
            var t2 = System.currentTimeMillis();
            System.out.println("joined 1M in " + (t2 - t1) + "ms");
        }

    }
}
