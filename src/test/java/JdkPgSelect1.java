import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;

public class JdkPgSelect1 {
    // 1T           16T
    // 1 - 117K     100K
    // 2 - 270K
    // 3 - 313K
    // 4 - 311K
    // 8 - 313K
    // 16 - 310K
    // 32 - 320K
    // 64 - 320K
    // 80 - 320K
    static int N_IO = 84;
    static int counter = 0;
    static long ts1 = System.currentTimeMillis();

    static void main() throws InterruptedException {
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "12");

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
                    if (counter++ == 1_000_000) {
                        counter = 0;
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
