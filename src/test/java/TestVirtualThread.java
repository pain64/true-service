import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.LongAdder;

public class TestVirtualThread {
    static AtomicLong l1 = new AtomicLong(0);
    static AtomicLong l2 = new AtomicLong(0);
    static AtomicLong l3 = new AtomicLong(0);
    static AtomicLong l4 = new AtomicLong(0);
    static AtomicLong l5 = new AtomicLong(0);
    static AtomicLong l6 = new AtomicLong(0);
    static AtomicLong l7 = new AtomicLong(0);
    static AtomicLong l8 = new AtomicLong(0);
    static LongAdder counter = new LongAdder();

    static void main() {
        new Thread(() -> {
            while(true) {
                System.out.println(l1.get());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for(var i = 0; i < Integer.MAX_VALUE; i++) {
                var j = i;
                executor.submit(() -> {
                    switch (j % 8) {
                        case 0: l1.incrementAndGet(); break;
                        case 1: l2.incrementAndGet(); break;
                        case 2: l3.incrementAndGet(); break;
                        case 3: l4.incrementAndGet(); break;
                        case 4: l5.incrementAndGet(); break;
                        case 5: l6.incrementAndGet(); break;
                        case 6: l7.incrementAndGet(); break;
                        case 7: l8.incrementAndGet(); break;
                    }
                });
            }
        }
    }
}
