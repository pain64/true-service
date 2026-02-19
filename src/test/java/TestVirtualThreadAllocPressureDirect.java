import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ForkJoinPool;

public class TestVirtualThreadAllocPressureDirect {
    // --add-opens java.base/java.lang=ALL-UNNAMED

    static volatile int counter = 0;
    static void main() throws InterruptedException {
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");

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
        var fjpp = fjp;

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
        var threadBuilderr = threadBuilder;

        while (true) {
            threadBuilder.start(() -> {
                var t1 = System.currentTimeMillis();

                //fjpp.externalSubmit()

                while (true) {
                    for (var i = 0; i < 10; i++)
                        fjpp.submit(() -> {
 //                           try {
                                var tt = threadBuilderr.start(() -> {
                                    counter++;
                                });
                                //tt.join();
//                            } catch (InterruptedException e) {
//                                throw new RuntimeException(e);
//                            }

                        });

                    if (counter > 1_000_000) {
                        counter = 0;
                        var t2 = System.currentTimeMillis();
                        System.out.println("joined 1M in " + (t2 - t1) + "ms");
                        t1 = t2;
                    }
                    Thread.yield();
                }
            }).join();
        }
    }
}
