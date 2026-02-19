import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;

public class TestVirtualThreadPg {

    // docker run -p 5433:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=test postgres

    static void main() throws InterruptedException, SQLException {
        System.out.println(
            System.getProperty("jdk.virtualThreadScheduler.parallelism")
        );
        int nThreads = 4;

        var connections = new Connection[nThreads];
        for (var i = 0; i < nThreads; i++)
            connections[i] = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5433/postgres",
                "postgres", "mysecretpassword"
            );

        var threads = new Thread[nThreads];
        var t1 = System.currentTimeMillis();

        for (var i = 0; i < nThreads; i++) {
            var ii = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                try {
                    connections[ii].createStatement()
                        .execute("select pg_sleep(10)");

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                var t2 = System.currentTimeMillis();
                System.out.println("elapsed: " + (t2 - t1));
            });
        }

        for (var i = 0; i < nThreads; i++)
            threads[i].join();
    }
}
