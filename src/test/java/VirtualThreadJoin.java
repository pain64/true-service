public class VirtualThreadJoin {
    static void main() throws InterruptedException {
        // все таски должны быть всегда parked кроме одной!!!
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        Thread.ofVirtual().start(() -> {
            var t1 = Thread.ofVirtual().start(() -> {
                System.out.println("t1");
            });

            var t2 = Thread.ofVirtual().start(() -> {
                System.out.println("t2");
            });

            try {
                t2.join();
                t1.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).join();
    }
}
