import java.util.concurrent.atomic.LongAdder;

public class TestVirtualThread2 {
    static LongAdder la = new LongAdder();

    static void main() throws InterruptedException {
        var threads = new Thread[10];

        for (var j = 0; j < Integer.MAX_VALUE; j++) {
            var tm1 = System.nanoTime();

            for (var i = 0; i < 10; i++) {
                threads[i] = Thread.ofVirtual().start(() -> {
                  la.add(1);
                });
            }

            for (var i = 0; i < 10; i++) {
                threads[i].join();
            }

            var tm2 = System.nanoTime();
            System.out.println(tm2 - tm1);
        }

        System.out.println("here2");
    }
}
