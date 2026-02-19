import java.util.concurrent.atomic.AtomicLongArray;

public class TestQueueVolatile1to1 {

    static final int pad = 16;
    static final int N_THREADS = 1;

    static void main() throws InterruptedException {
        var slotsIn = new AtomicLongArray(N_THREADS * pad);
        var slotsOut = new AtomicLongArray(N_THREADS * pad);

        for (var i = 0; i < N_THREADS; i++) {
            var ii = i;
            var execT = new Thread(() -> {
                var expected = 1;

                while (true) {
                    if (slotsIn.getAcquire(ii * pad) == expected) {
                        expected = expected == 0 ? 1 : 0;
                        slotsOut.setRelease(ii * pad, expected);
                    }
                }
            });
            execT.start();
        }

        var ioT = new Thread(() -> {
            var t1 = System.currentTimeMillis();
            var count = 0;

            while(true) {
                var expected = new long[N_THREADS];

                for (var i = 0; i < N_THREADS; i++) {
                    if (slotsOut.getAcquire(i * pad) == expected[i]) {
                        expected[i] = expected[i] == 0 ? 1 : 0;
                        count++;
                        slotsIn.setRelease(i * pad, expected[i]);


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

        ioT.start();
        ioT.join();
    }
}

