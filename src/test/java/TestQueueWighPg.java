import com.zaxxer.hikari.HikariDataSource;
import org.jctools.queues.SpscArrayQueue;
import org.jctools.queues.atomic.SpscAtomicArrayQueue;

import java.sql.SQLException;
import java.util.ArrayList;

public class TestQueueWighPg {

    // docker run -p 5433:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=test postgres

    static int N_THREADS = 80;
    static Long instance = 42L;

    static void main() throws InterruptedException {
        var ds = new HikariDataSource() {{
            setJdbcUrl("jdbc:postgresql://localhost:5432/petclinic?binaryTransfer=false&sslmode=disable");
            setUsername("sa");
            setPassword("1234");
            setMaximumPoolSize(N_THREADS);
        }};

        System.out.println(
            System.getProperty("jdk.virtualThreadScheduler.parallelism")
        );

        var qIn = new ArrayList<SpscAtomicArrayQueue<Long>>();
        var qOut = new ArrayList<SpscAtomicArrayQueue<Long>>();

        for (var i = 0; i < N_THREADS; i++) {
            qIn.add(new SpscAtomicArrayQueue<>(20));
            qOut.add(new SpscAtomicArrayQueue<>(20));
        }

        for (var i = 0; i < N_THREADS; i++) {
            var in = qIn.get(i);
            var out = qOut.get(i);

            Thread.ofVirtual().start(() -> {
                while (true) {
                    var v = in.poll();
                    if (v != null) {
                        try (var cn = ds.getConnection()) {
                            cn.createStatement().execute("select 1");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        while (!out.offer(v)) ;
                    }
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

