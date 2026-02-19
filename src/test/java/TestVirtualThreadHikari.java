import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestVirtualThreadHikari {

    // docker run -p 5433:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=test postgres

    static void main() throws InterruptedException, SQLException {
        var ds = new HikariDataSource() {{
            setJdbcUrl("jdbc:postgresql://localhost:5433/postgres");
            setUsername("postgres");
            setPassword("mysecretpassword");
            setMaximumPoolSize(2);
        }};

        // 1
        System.out.println(
            System.getProperty("jdk.virtualThreadScheduler.parallelism")
        );
        int nThreads = 4;


        var threads = new Thread[nThreads];
        var t1 = System.currentTimeMillis();

        for (var i = 0; i < nThreads; i++)
            threads[i] = Thread.ofVirtual().start(() -> {
                try (var cn = ds.getConnection()) {

                    cn.createStatement()
                        .execute("select pg_sleep(10)");

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                var t2 = System.currentTimeMillis();
                System.out.println("elapsed: " + (t2 - t1));
            });


        for (var i = 0; i < nThreads; i++)
            threads[i].join();
    }
}
