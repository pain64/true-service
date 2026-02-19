import java.util.concurrent.Executors;

public class TestVirtualThreadSchedule {
    static void main() throws InterruptedException {

        System.out.println(
            System.getProperty("jdk.virtualThreadScheduler.parallelism")
        );
        int nThreads = 512;
        var threads = new Thread[nThreads];
        var t1 = System.currentTimeMillis();

        for (var i = 0; i < nThreads; i++)
            threads[i] = Thread.ofVirtual().start(() -> {
                var start = t1;
                for (var j = 0L; j < 10_000_000_000L; j++) start *= 3;
                var t2 = System.currentTimeMillis();
                System.out.println("elapsed: " + (t2 - t1) + " v:" + start);
            });

        for (var i = 0; i < nThreads; i++)
            threads[i].join();
    }
}
