public class TestVirtualThreadAllocPressureNoJoin2 {
    static volatile int counter = 0;
    static void main() throws InterruptedException {
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");

        while (true) {
            var builder = Thread.ofVirtual();
            Thread.ofVirtual().start(() -> {
                var t1 = System.currentTimeMillis();

                while (true) {
                    for (var i = 0; i < 1; i++)
                        builder.start(() -> {
                            counter++;
                        });

                    if (counter > 1_000_000) {
                        counter = 0;
                        var t2 = System.currentTimeMillis();
                        System.out.println("joined 1M in " + (t2 - t1) + "ms");
                        t1 = t2;
                    }
                    Thread.yield();
                }
            }).join();
        }
    }
}
