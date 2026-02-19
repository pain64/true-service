
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardVirtualThreadExecutor;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class Test4TomcatHello {

    @Test public void test() {

        var ds = new HikariDataSource() {{
            setJdbcUrl("jdbc:postgresql://localhost:5432/petclinic?binaryTransfer=false&sslmode=disable");
            setUsername("sa");
            setPassword("1234");
            setMaximumPoolSize(80);
        }};

        System.out.println("time: " + ManagementFactory.getRuntimeMXBean().getUptime() + "ms");

        var tomcat = new Tomcat();
        tomcat.setPort(1111);
        var connector = tomcat.getConnector();
        var executor = new StandardVirtualThreadExecutor();
        try {
            executor.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
        connector.getProtocolHandler().setExecutor(executor);
        //connector.setProperty()

        var ctx = tomcat.addContext("", new File(".").getAbsolutePath());

        Tomcat.addServlet(ctx, "hello", new HttpServlet() {
            @Override protected void service(
                HttpServletRequest request, HttpServletResponse response
            ) throws IOException {

//                try (
//                    var cn = ds.getConnection();
//                    var stmt = cn.prepareStatement("select 1")
//                ) {
//                    stmt.execute();
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
                try (var out = response.getOutputStream()) {
                    out.println("Hello, world");
                }
            }
        });

        ctx.addServletMappingDecoded("/*", "hello");

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }

        System.out.println("time: " + ManagementFactory.getRuntimeMXBean().getUptime() + "ms");

        tomcat.getServer().await();
    }
}
