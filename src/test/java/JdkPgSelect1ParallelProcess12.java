import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class JdkPgSelect1ParallelProcess12 {
    //     1T        2T    4T     8T    16T
    // 1 - 117K      112K  100K         100K
    // 2 - 270K      217K  197K         181K
    // 3 - 313K      355K  295K         256K
    // 4 - 311K      476K  398K         280K
    // 8 - 313K      566K  714K         454K
    // 16 - 310K     549K  691K         750K
    // 32 - 320K     549K  677K         1.02M
    // 64 - 320K     609K  747K         1.35M
    // 80 - 320K     657K  833K         1.69M
    // 100           618K  900K         1.40M

    // 2 - 544K
    // 4 - 754K
    static int N_IO = 5;
    static AtomicInteger counter = new AtomicInteger(0);
    static long ts1 = System.currentTimeMillis();

    static void main() throws InterruptedException {
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");

        var ds = new HikariDataSource() {{
            setJdbcUrl("jdbc:postgresql://localhost:5432/petclinic?binaryTransfer=false&sslmode=disable");
            setUsername("sa");
            setPassword("1234");
            setMaximumPoolSize(N_IO);
        }};

        for (var i = 0; i < N_IO; i++) {
            Thread.ofVirtual().start(() -> {
                while (true) {
                    try (
                        var cn = ds.getConnection();
                        var stmt = cn.prepareStatement("select 1");
                        var rs = stmt.executeQuery()
                    ) {
                        rs.next();
                        if (rs.getInt(1) != 1) throw new RuntimeException("???");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    if (counter.incrementAndGet() == 1_000_000) {
                        counter.set(0);
                        var ts2 = System.currentTimeMillis();
                        System.out.println("executed 1M in " + (ts2 - ts1) + "ms");
                        ts1 = ts2;
                    }
                }
            });
        }
        Thread.sleep(10000000000L);
    }
}
