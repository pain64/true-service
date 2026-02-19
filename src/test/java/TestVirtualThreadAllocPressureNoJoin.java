import java.util.concurrent.atomic.AtomicLong;

public class TestVirtualThreadAllocPressureNoJoin {
    static volatile int counter = 0;
    static void main() throws InterruptedException {
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");

        while (true) {
            // 160ms чтобы прогнать 1M тасков
            var t1 = System.currentTimeMillis();


            Thread.ofVirtual().start(() -> {
                for (var i = 0; i < 1_000_000; i++)
                    Thread.ofVirtual().start(() -> {
                        counter++;
                    });

                while (counter != 1_000_000) Thread.yield();
                counter = 0;
            }).join();


            var t2 = System.currentTimeMillis();
            System.out.println("joined 1M in " + (t2 - t1) + "ms");
        }
    }
}
