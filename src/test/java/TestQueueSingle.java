import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.SpmcArrayQueue;
import org.jctools.queues.SpscArrayQueue;

import java.util.ArrayList;

public class TestQueueSingle {
    static int N_THREADS = 80;
    static Long instance = 42L;

    static void main() throws InterruptedException {
        var qIn = new ArrayList<SpscArrayQueue<Long>>();
        var qOut = new ArrayList<SpscArrayQueue<Long>>();

        for (var i = 0; i < N_THREADS; i++) {
            qIn.add(new SpscArrayQueue<>(20));
            qOut.add(new SpscArrayQueue<>(20));
        }

        for (var i = 0; i < N_THREADS; i++) {
            var in = qIn.get(i);
            var out = qOut.get(i);

            Thread.ofVirtual().start(() -> {
                while (true) {
                    var v = in.poll();
                    if (v != null)
                        while (!out.offer(v)) ;
                }
            });
        }

        var ioThread = new Thread(() -> {
            var t1 = System.currentTimeMillis();

            var count = 0;
            while (true) {
                for (var i = 0; i < N_THREADS; i++) {
                    var in = qIn.get(i);
                    var out = qOut.get(i);

                    while (in.offer(instance)) ;
                    while (true) {
                        var v = out.poll();
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
            }
        });

        ioThread.start();
        ioThread.join();
    }
}

