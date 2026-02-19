import com.zaxxer.hikari.HikariDataSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;

public class TestVirtualThreadHikariNodejsLike {

    // docker run -p 5433:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=test postgres

    static int N_THREADS = 16;

    static void main() throws
        InterruptedException, SQLException, ClassNotFoundException,
        InvocationTargetException, InstantiationException,
        IllegalAccessException, NoSuchMethodException {

        System.setProperty("jdk.pollerMode", "SYSTEM_THREADS");
        System.setProperty("jdk.readPollers", "1");
        System.setProperty("jdk.writePollers", "1");
        // jdk.readPollers


        var ds = new HikariDataSource() {{
            setJdbcUrl("jdbc:postgresql://localhost:5432/petclinic?binaryTransfer=false&sslmode=disable");
            setUsername("sa");
            setPassword("1234");
            setMaximumPoolSize(96);
        }};

//        var connections = new Connection[96];
//        for (var i = 0; i < 96; i++)
//            connections[i] = DriverManager.getConnection(
//                "jdbc:postgresql://localhost:5432/petclinic?binaryTransfer=false&sslmode=disable",
//                "sa", "1234"
//            );


        var threads = new Thread[12];
        var lt = new Thread[96];

        for (var a = 0; a < 12; a++) {
            var aa = a;
            threads[a] =  new Thread(() -> {
                System.out.println("construct thread");



                Class<?> cl1 = null;
                try {
                    cl1 = Class.forName("java.lang.VirtualThread");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                Method m1 = null;
                try {
                    m1 = cl1.getDeclaredMethod("createDefaultScheduler");
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                m1.setAccessible(true);

                System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
                System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");

                ForkJoinPool fjp = null;
                try {
                    fjp = (ForkJoinPool) m1.invoke(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

                Class<?> cl = null;
                try {
                    cl = Class.forName("java.lang.ThreadBuilders$VirtualThreadBuilder");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                var cons = cl.getDeclaredConstructors()[1];
                cons.setAccessible(true);
                Thread.Builder.OfVirtual threadBuilder = null;
                try {
                    threadBuilder = (Thread.Builder.OfVirtual) cons.newInstance(fjp);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

                threadBuilder = threadBuilder.inheritInheritableThreadLocals(true);



                for (var i = 0; i < 8; i++) {
                    System.out.println("submit " + aa + " " + i);
                    var ii = i;
                    lt[aa * 8 + i] = threadBuilder.start(() -> {
                        System.out.println("start " + aa + " " + ii);
                        var now = System.currentTimeMillis();
                        for (var j = 0L; j < 1_00000_0L; j++) {
                            now *= 2;

                            try (var cn = ds.getConnection())  {
                                // System.out.println("exec " + aa + " " + ii);
                                //connections[aa * 8 + ii].createStatement().execute("select 1");
                                cn.createStatement().execute("select 1");
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        System.out.println("result " + now);
                    });
                }

//                try {
//                    Thread.sleep(100_000_00);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }

                var xx = 1;

                for (var i = 0; i < 8; i++) {
                    try {
                        System.out.println("join " + aa + " " + i);
                        lt[aa * 8 + i].join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            threads[a].start();
            //Thread.sleep(5000);
        }


        var t1 = System.currentTimeMillis();
        //Thread.sleep(10000000);

        for (var i = 0; i < 12; i++)
            threads[i].join();

        var t2 = System.currentTimeMillis();
        System.out.println("elapsed: " + (t2 - t1));
    }
}
