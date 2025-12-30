package dev.yooproject.funitems.async;

import dev.yooproject.funitems.util.DebugUtil;

import java.util.concurrent.*;

public class AsyncThreadPoolExecutor implements AsyncExecutor {

    private final ScheduledExecutorService executorService;

    public AsyncThreadPoolExecutor(int threads, String namePrefix) {
        this.executorService = Executors.newScheduledThreadPool(
                threads,
                runnable -> {
                    Thread t = new Thread(runnable);
                    t.setName(namePrefix + "-" + t.getId());
                    t.setDaemon(true);
                    return t;
                }
        );
    }

    @Override
    public CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(() -> {
            try {
                task.run();
            } catch (Exception e) {
                DebugUtil.exception("AsyncThreadPoolExecutor", e);
            }
        }, executorService);
    }

    @Override
    public <T> CompletableFuture<T> runAsync(SupplierWithException<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.get();
            } catch (Exception e) {
                DebugUtil.exception("AsyncThreadPoolExecutor", e);
                return null;
            }
        }, executorService);
    }

    @Override
    public ScheduledFuture<?> runRepeating(Runnable task, long initialDelayMillis, long periodMillis) {
        return executorService.scheduleAtFixedRate(() -> {
            try {
                task.run();
            } catch (Exception e) {
                DebugUtil.exception("AsyncThreadPoolExecutor", e);
            }
        }, initialDelayMillis, periodMillis, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> runDelayed(Runnable task, long delayMillis) {
        return executorService.schedule(() -> {
            try {
                task.run();
            } catch (Exception e) {
                DebugUtil.exception("AsyncThreadPoolExecutor", e);
            }
        }, delayMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown() {
        executorService.shutdownNow();
    }
}
