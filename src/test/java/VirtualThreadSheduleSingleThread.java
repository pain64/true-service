import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.LockSupport;

public class VirtualThreadSheduleSingleThread {
    static void main() throws InterruptedException {
//        var mt = new Thread[2];
//
//        for (var k = 0; k < 2; k++) {
//            var kk = k;
        var kk = 0;
        //mt[kk] = new Thread(() -> {
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
        var threads = new Thread[9];
        var unblock = new boolean[9];
        var ready = new int[1];

        var wt = threadBuilder.start(() -> {
            while (ready[0] != 9)
                LockSupport.park();

            var mode = 0;
            while (true) {
                for (var i = 0; i < 9; i += mode % 2 + 1) {
                    unblock[i] = true;
                    LockSupport.unpark(threads[i]);
                }
//                        if (kk == 0)
//                            System.out.println(kk + " wakeup job");
                mode++;
                Thread.yield();
            }
        });


        for (var i = 0; i < 8; i++) {
            var ii = i;
            threads[i] = threadBuilder.start(() -> {
                ready[0]++;
                LockSupport.unpark(wt);

                while (true) {
                    while (!unblock[ii])
                        LockSupport.park();
//                            if (kk == 0)
                    System.out.println(kk + " do job-" + ii);
                    unblock[ii] = false;
                }
            });
        }

        Thread.sleep(1000);

        threads[8] = threadBuilder.start(() -> {
                        ready[0]++;
                        LockSupport.unpark(wt);

            while (true) {
                while (!unblock[8])
                    LockSupport.park();
                if (kk == 0)
                    System.out.println(kk + " do job-" + 8);
                unblock[8] = false;
            }
        });





//                try {
//                    wt.join();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
        // });

        //mt[kk].start();
        //}


        //mt[0].join();
        //mt[1].join();
        Thread.sleep(100000000);
    }
}
