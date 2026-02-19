import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLongArray;

public class TestQueueWighVirtual {

    // docker run -p 5433:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=test postgres

    static int N_THREADS = 80;
    static int pad = 16;

    static void main() throws InterruptedException {
        var slots = new AtomicLongArray(N_THREADS * pad);

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
            Thread.ofVirtual().start(() -> {
                while (true) {
                    while (slots.get(ii * pad) == 0)
                        Thread.onSpinWait();

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
                    if (slots.getAcquire(i * pad) == 0)
                        count++;
                    slots.setRelease(i * pad, 1);

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

