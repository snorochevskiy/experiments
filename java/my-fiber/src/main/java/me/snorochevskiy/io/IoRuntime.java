package me.snorochevskiy.io;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class IoRuntime {

    // Queue of fibers to be executed
    private BlockingDeque<IoFiber> fibers = new LinkedBlockingDeque<>();

    // Fibers that are suspended because of waiting on blocking/non-blocking operation
    private Set<IoFiber> suspended = new HashSet<>();

    private ThreadFactory threadFactory = r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    };

    // The thread fibers are executed on
    private ExecutorService worker = Executors.newSingleThreadExecutor(threadFactory);

    // Thread pool for blocking operations
    private ExecutorService blockingPool = Executors.newCachedThreadPool(threadFactory);

    public IoRuntime() {
        worker.submit(() -> {
            try {
                while (true) {
                    IoFiber<? super Object> fiber = fibers.take();
                    switch (fiber.exec()) {
                        case IoFiber.Done<?>(Object result):
                            fiber.getCF().complete(result);
                            break;
                        case IoFiber.Suspended<?>():
                            break;
                        case IoFiber.NeedShift<?>():
                            fibers.add(fiber); // adding to the end of queue
                            break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <A> A execSync(Io<A> io) {
        var future = execAsync(io);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public <A> CompletableFuture<A> execAsync(Io<A> io) {
        var fiber = new IoFiber<A>(io, this);
        fibers.add(fiber);
        return fiber.getCF();
    }

    void runBlocking(Runnable runnable, IoFiber<?> fiber) {
        suspended.add(fiber);
        var future = CompletableFuture.runAsync(runnable, blockingPool);
        future.thenRun(() -> {
            suspended.remove(fiber);
            fibers.add(fiber);
        });
    }

    void waitCompletableFuture(CompletableFuture<?> cf, IoFiber<?> fiber) {
        suspended.add(fiber);
        cf.thenRun(() -> {
            suspended.remove(fiber);
            fibers.add(fiber);
        });
    }
}