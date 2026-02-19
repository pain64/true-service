import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.LockSupport;

public class TestQueueWighVirtualParkNodeLikeNoPool2 {

    // docker run -p 5433:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=test postgres

    static int N_SERVERS = 1;
    static int N_IO = 80;

    static void main() throws InterruptedException, SQLException {
        var connections = new Connection[N_SERVERS * N_IO];
        for (var i = 0; i < connections.length; i++)
            connections[i] = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/petclinic?binaryTransfer=false&sslmode=disable",
                "sa", "1234"
            );

//        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
//        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");
//        System.setProperty("jdk.readPollers", "1");
//        System.setProperty("jdk.writePollers", "1");
//
//        var t = Thread.ofVirtual().start(() -> {
//
//
//            try (var stmt = connections[0].prepareStatement("select 1")) {
//                stmt.execute();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        t.join();


        System.setProperty("jdk.pollerMode", "SYSTEM_THREADS");
        System.setProperty("jdk.readPollers", "1");
        System.setProperty("jdk.writePollers", "1");




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

                        // take task from io_uring cqe
//                        while (slots[ii] != 1)
//                            LockSupport.park();

                        var t1 = System.currentTimeMillis();

                        for (var k = 0; k < 100_000; k++) {
                            var cn = connections[ss * N_IO + ii];

                            try (var stmt = cn.prepareStatement("select 1")) {
                                stmt.execute();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

//                            slots[ii] = 0;
//                            LockSupport.unpark(ioThread[0]);
                        }

                        var t2 = System.currentTimeMillis();

                        System.out.println("executed 100K in " + (t2 - t1));
                        t1 = t2;
                    }
                });
            }
        }

        Thread.sleep(1000000000);
    }
    
    // 1. Научиться создавать и биндить сокеты через libc => получать FD
    // 2. Сделать SocketFactory
    // 3. Сделать Socket
    // 4. Сделать InputStream поверх MemorySegment
    // 5. Организовать event loop и park unpark
    // 6. Проверить сколько это даст перформанса?
}

