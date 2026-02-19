import org.jctools.queues.atomic.MpscAtomicArrayQueue;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;

public class VirtualThreadSheduleManualPerf {
    static class MyExecutor implements Executor {
        private final MpscAtomicArrayQueue<Runnable> queue =
            new MpscAtomicArrayQueue<>(128);

        public MyExecutor() {
            new Thread(() -> {
                while (true) {
                    var task = queue.poll();
                    if (task != null)
                        task.run();
                }
            }).start();
        }

        @Override public void execute(@NotNull Runnable runnable) {
            while(!queue.offer(runnable));
        }
    }

    static long counter1 = 0;

    static void main() throws InterruptedException {


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
            threadBuilder = (Thread.Builder.OfVirtual) cons.newInstance(new MyExecutor());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        threadBuilder = threadBuilder.inheritInheritableThreadLocals(true);

        threadBuilder.start(() -> {
            var t1 = System.currentTimeMillis();
            while (true) {
                counter1++;
                Thread.ofVirtual().start(() -> {});
                if (counter1 > 1_000_000) {
                    counter1 = 0;
                    var t2 = System.currentTimeMillis();
                    System.out.println("switched 1M in " + (t2 - t1) + "ms");
                    t1 = t2;
                }
                Thread.yield();
            }
        }).join();
    }
}
