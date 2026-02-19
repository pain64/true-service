import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.locks.LockSupport;

public class TestQueueWighVirtualPark {

    // docker run -p 5433:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=test postgres

    static int N_THREADS = 80;
    static int pad = 16;

    static void main() throws InterruptedException {
        var ioThread = new Thread[1];
        var threads = new Thread[N_THREADS];
        var slots = new AtomicLongArray(N_THREADS * pad);
        var locks = new AtomicLongArray(N_THREADS * pad);

        var ds = new HikariDataSource() {{
            setJdbcUrl("jdbc:postgresql://localhost:5432/petclinic?binaryTransfer=false&sslmode=disable");
            setUsername("sa");
            setPassword("1234");
            setMaximumPoolSize(N_THREADS);
        }};

        System.out.println(
            System.getProperty("jdk.virtualThreadScheduler.parallelism")
        );


        for (var i = 0; i < N_THREADS; i++) {
            var ii = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                while (true) {
                    LockSupport.park();

                    while (slots.getAcquire(ii * pad) != 1)
                        Thread.onSpinWait();

                    try (
                        var cn = ds.getConnection();
                        var stmt = cn.prepareStatement("select 1")
                    ) {

                        stmt.execute();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    slots.setRelease(ii * pad, 0);
                    LockSupport.unpark(ioThread[0]);
                }
            });
        }

        ioThread[0] = Thread.ofVirtual().start(() -> {
            var t1 = System.currentTimeMillis();

            var count = 0;
            while (true) {
                // read -> readMore
                // write -> writeMore
                //
                // energy savings
                //     - if cqe is empty
                //     -
                var countBefore = count;
                for (var i = 0; i < N_THREADS; i++) {
                    if (slots.getAcquire(i * pad) == 0) {
                        slots.setRelease(i * pad, 1);
                        LockSupport.unpark(threads[i]);
                        count++;
                    }

                    if (count == 10_000_00) {
                        var t2 = System.currentTimeMillis();
                        System.out.println(
                            "enqueued 10M in: " + (t2 - t1) + "ms"
                        );
                        count = 0;
                        t1 = t2;
                    }
                }

                if (countBefore == count) {
                    LockSupport.park();
                    //LockSupport.parkNanos(100);
                    // System.out.println("yield!");
                    // Thread.yield();
                }
            }
        });

        //ioThread.start();
        ioThread[0].join();
    }
}

