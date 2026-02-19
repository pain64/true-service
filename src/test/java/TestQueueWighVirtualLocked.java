import com.zaxxer.hikari.HikariDataSource;
import org.wildfly.common.lock.Locks;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.locks.LockSupport;

public class TestQueueWighVirtualLocked {

    // docker run -p 5433:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=test postgres

    static int N_THREADS = 80;
    static int pad = 16;

    static void main() throws InterruptedException {
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
                    var canWork = 0L;
                    while (canWork == 0L) {
                        for (var j = 0; j < 2; j++) {
                            canWork = slots.getAcquire(ii * pad);
                            if (canWork == 1) break;
                            Thread.onSpinWait();
                        }

                        if (canWork == 1) break;
                        // Thread.yield();

                        LockSupport.parkNanos(1_000_000);
                    }

                    try (var cn = ds.getConnection()) {
                        cn.createStatement().execute("select 1");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    slots.setRelease(ii * pad, 0);
                }
            });
        }

        var ioThread = new Thread(() -> {
            var t1 = System.currentTimeMillis();

            var count = 0;
            while (true) {
                for (var i = 0; i < N_THREADS; i++) {
                    if (slots.getAcquire(i * pad) == 0) {
                        slots.setRelease(i * pad, 1);
                        LockSupport.unpark(threads[i]);
                        count++;
                    }

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

