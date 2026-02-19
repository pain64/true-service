import net.truej.service.low.IoUringEventLoop;
import net.truej.service.low.MySocketFactory;
import org.jctools.queues.atomic.MpscAtomicArrayQueue;
import org.jetbrains.annotations.NotNull;
import org.postgresql.Driver;
import org.postgresql.ds.PGSimpleDataSource;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class TestQueueWighVirtualParkNodeLikeNoPool3 {
    static AtomicInteger counter = new AtomicInteger(0);
    static volatile long ts1 = System.currentTimeMillis();

    static class MyExecutor implements Executor {
        private final MpscAtomicArrayQueue<Runnable> queue =
            new MpscAtomicArrayQueue<>(128);

        public MyExecutor(IoUringEventLoop eventLoop) {
            new Thread(() -> {
                while (true) {
                    var task = queue.poll();
                    if (task != null) task.run();
                    else {
                        // we have no tasks
                        eventLoop.processAllEvents();
                    }
                }
            }).start();
        }

        @Override public void execute(@NotNull Runnable runnable) {
            while (!queue.offer(runnable)) ;
        }
    }

    // docker run -p 5433:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=test postgres

    static int N_SERVERS = 12;
    static int N_IO = 8;

    // endpointFunction() {
    //
    // }


    static void main() throws InterruptedException, SQLException {
        var pgDriver = new Driver();
        var lock = new ReentrantLock();


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


        //System.setProperty("jdk.pollerMode", "SYSTEM_THREADS");
        //System.setProperty("jdk.readPollers", "1");
        //System.setProperty("jdk.writePollers", "1");


        System.out.println(
            System.getProperty("jdk.virtualThreadScheduler.parallelism")
        );

        for (var s = 0; s < N_SERVERS; s++) {
            //Thread.sleep(3000);
            var ss = s;

//            Class<?> cl1 = null;
//            try {
//                cl1 = Class.forName("java.lang.VirtualThread");
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//            Method m1 = null;
//            try {
//                m1 = cl1.getDeclaredMethod("createDefaultScheduler");
//            } catch (NoSuchMethodException e) {
//                throw new RuntimeException(e);
//            }
//            m1.setAccessible(true);
//
//            System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
//            System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");
//
//            ForkJoinPool fjp = null;
//            try {
//                fjp = (ForkJoinPool) m1.invoke(null);
//
////                    var f = fjp.getClass().getDeclaredField("config");
////                    f.setAccessible(true);
////                    var cfg = (Long) f.get(fjp);
////                    cfg = cfg - 1;
////                    f.set(fjp, cfg);
//
//                var f2 = fjp.getClass().getDeclaredField("queues");
//                f2.setAccessible(true);
//                f2.set(fjp, Array.newInstance(Class.forName("java.util.concurrent.ForkJoinPool$WorkQueue"), 1));
//
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            } catch (InvocationTargetException
//                     | NoSuchFieldException | ClassNotFoundException
//                e
//            ) {
//                throw new RuntimeException(e);
//            }

            var eventLoop = new IoUringEventLoop();

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
                // threadBuilder = (Thread.Builder.OfVirtual) cons.newInstance(fjp);
                threadBuilder = (Thread.Builder.OfVirtual) cons.newInstance(new MyExecutor(eventLoop));
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }


            //System.out.println(eventLoop);
            //
            threadBuilder = threadBuilder
                .inheritInheritableThreadLocals(true)
                .uncaughtExceptionHandler((t, e) -> {
                    e.printStackTrace();
                    System.exit(1);
                });


            // Внутри делать bind ???
            // Создавать сервер ???


            var threads = new Thread[N_IO];


            for (var i = 0; i < N_IO; i++) {
                var ii = i;

                threads[i] = threadBuilder.start(() -> {
                    System.out.println("start thread");

                    MySocketFactory.loops.put(ss + "_" + ii, eventLoop);
                    Connection cn;
                    try {
                        lock.lock();
                        System.out.println("before get connection!");


                        // cn = ds.getConnection("sa", "1234");
                        cn = pgDriver.connect(
                            "jdbc:postgresql://localhost:5432/petclinic?binaryTransfer=false" +
                            "&sslmode=disable&socketFactory=net.truej.service.low.MySocketFactory" +
                            "&socketFactoryArg=" + ss + "_" + ii,

                            new Properties() {{
                                put("user", "sa");
                                put("password", "1234");
                            }});

                        System.out.println("after get connection:" + cn);
                    } catch (SQLException e) {
                        System.out.println("oops");
                        throw new RuntimeException(e);
                    } finally {
                        lock.unlock();
                    }



                    while (true) {
                        try (var stmt = cn.prepareStatement("select 1")) {
                            var rs = stmt.executeQuery();
                            rs.next();
                            var yy = rs.getInt(1);
                            rs.close();
                            // System.out.println("executed once");
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
        }

        Thread.sleep(1000000000);
    }
}

