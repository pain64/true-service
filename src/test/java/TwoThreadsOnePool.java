import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ForkJoinPool;

public class TwoThreadsOnePool {
    // --add-opens java.base/java.lang=ALL-UNNAMED
    // --add-opens java.base/java.util.concurrent=ALL-UNNAMED
    static long counter1 = 0;
    static long counter2 = 0;
    static long counter3 = 0;

    static void main() throws InterruptedException {
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        //System.setProperty("jdk.virtualThreadScheduler.parallelism", "3");

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
//            var f = fjp.getClass().getDeclaredField("config");
//            f.setAccessible(true);
//            var cfg = (Long) f.get(fjp);
//            cfg = cfg - 1;
//            f.set(fjp, cfg);
            var f = fjp.getClass().getDeclaredField("queues");
            f.setAccessible(true);
            f.set(fjp, Array.newInstance(Class.forName("java.util.concurrent.ForkJoinPool$WorkQueue"), 1));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException |
                 ClassNotFoundException e) {
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

        //threadBuilder = threadBuilder.inheritInheritableThreadLocals(true);

        threadBuilder.start(() -> {
            while (true) {
                counter1++;
                Thread.yield();
            }
        });

        threadBuilder.start(() -> {
            while (true) {
                counter2++;
                Thread.yield();
            }
        });

        while (true) {

            threadBuilder.start(() -> {
                counter3++;
            }).join();

            System.out.println(
                "counter1 = " + counter1 + "\n" +
                "counter2 = " + counter2 + "\n" +
                "counter3 = " + counter3
            );

            Thread.sleep(1000);
        }
    }
}
