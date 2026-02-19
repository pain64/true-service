import org.jctools.queues.atomic.MpscAtomicArrayQueue;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.LockSupport;

public class VirtualThreadSheduleManual {
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
    static long counter2 = 0;
    static long counter3 = 0;

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
            });

            System.out.println(
                "counter1 = " + counter1 + "\n" +
                "counter2 = " + counter2 + "\n" +
                "counter3 = " + counter3
            );

            Thread.sleep(1000);
        }
    }
}
