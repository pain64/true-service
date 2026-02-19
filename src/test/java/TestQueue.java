import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.SpmcArrayQueue;
import org.jctools.queues.SpscArrayQueue;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.LongAdder;

public class TestQueue {
    static Long instance = 42L;

    static void main() throws InterruptedException {
        var qIn = new SpmcArrayQueue<Long>(1000);
        var qOut = new MpscArrayQueue<Long>(1000);

        for (var i = 0; i < 31; i++)
            new Thread(() -> {
                while (true) {
                    var v = qIn.poll();
                    if (v != null)
                        while (!qOut.offer(v)) ;
                }
            }).start();

        var ioThread = new Thread(() -> {
            var t1 = System.currentTimeMillis();

            var count = 0;
            while (true) {
                while (qIn.offer(instance)) ;
                while (true) {
                    var v = qOut.poll();
                    if (v == null) break;
                    count++;
                    if (count == 10_000_000) {
                        var t2 = System.currentTimeMillis();
                        System.out.println(
                            "enqueued 10M in: " + (t2 - t1) + "ms"
                        );
                        count = 0;
                        t1 = t2;
                    }
                }
            }
        });

        ioThread.start();
        ioThread.join();
    }
}

