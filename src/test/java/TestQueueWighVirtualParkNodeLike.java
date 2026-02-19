import com.zaxxer.hikari.HikariDataSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.locks.LockSupport;

public class TestQueueWighVirtualParkNodeLike {

    // docker run -p 5433:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=test postgres

    static int N_SERVERS = 12;
    static int N_IO = 7;

    static void main() throws InterruptedException {
        System.setProperty("jdk.pollerMode", "SYSTEM_THREADS");
        System.setProperty("jdk.readPollers", "1");
        System.setProperty("jdk.writePollers", "1");



        var ds = new HikariDataSource() {{
            setJdbcUrl("jdbc:postgresql://localhost:5432/petclinic?binaryTransfer=false&sslmode=disable");
            setUsername("sa");
            setPassword("1234");
            setMaximumPoolSize(N_SERVERS * N_IO);
        }};

        System.out.println(
            System.getProperty("jdk.virtualThreadScheduler.parallelism")
        );

        for (var s = 0; s < N_SERVERS; s++) {


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

            var ss = s;
            var ioThread = new Thread[1];
            var threads = new Thread[N_IO];
            var slots = new long[N_IO];

            for (var i = 0; i < N_IO; i++) {
                var ii = i;
                threads[i] = threadBuilder.start(() -> {
                    while (true) {

                        while (slots[ii] != 1)
                            LockSupport.park();

                        try (var cn = ds.getConnection()) {
                            cn.createStatement().execute("select 1");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        slots[ii] = 0;
                        LockSupport.unpark(ioThread[0]);
                    }
                });
            }

            ioThread[0] = threadBuilder.start(() -> {


                var t1 = System.currentTimeMillis();

                var count = 0;
                while (true) {
                    var countBefore = count;
                    for (var i = 0; i < N_IO; i++) {
                        if (slots[i] == 0) {
                            slots[i]= 1;
                            LockSupport.unpark(threads[i]);
                            count++;
                        }

                        if (count == 1_000_00) {
                            var t2 = System.currentTimeMillis();
                            System.out.println(
                                "server" + ss + " enqueued 10M in: " + (t2 - t1) + "ms"
                            );
                            count = 0;
                            t1 = t2;
                        }
                    }

                    // vs kernel ???
                    // задачи, которые дает ядро
                    // задачи, которые мы ждем из пула
                    //   - все является pool-bound ???
                    // если есть новые sqe, то идем в ядро
                    //

                    // if (countBefore == count) {
                        LockSupport.park();
                        //LockSupport.parkNanos(100);
                        // System.out.println("yield!");
                        // Thread.yield();
                    //}
                }
            });
        }

        Thread.sleep(1000000000);
    }
}

