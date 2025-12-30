package dev.yooproject.funitems.async;

import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BukkitAsyncExecutor implements AsyncExecutor {

    private final Plugin plugin;

    public BukkitAsyncExecutor(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Void> runAsync(Runnable task) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                task.run();
                future.complete(null);
            } catch (Exception e) {
                DebugUtil.exception("BukkitAsyncExecutor", e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public <T> CompletableFuture<T> runAsync(SupplierWithException<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                future.complete(task.get());
            } catch (Exception e) {
                DebugUtil.exception("BukkitAsyncExecutor", e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public ScheduledFuture<?> runRepeating(Runnable task, long initialDelayMillis, long periodMillis) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin,
                () -> {
                    try {
                        task.run();
                    } catch (Exception e) {
                        DebugUtil.exception("BukkitAsyncExecutor", e);
                    }
                }, initialDelayMillis / 50, periodMillis / 50
        );
        return new ScheduledFuture<>() {
            @Override
            public long getDelay(TimeUnit unit) { return 0; }

            @Override
            public int compareTo(Delayed o) { return 0; }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                bukkitTask.cancel();
                return true;
            }

            @Override
            public boolean isCancelled() { return bukkitTask.isCancelled(); }

            @Override
            public boolean isDone() { return bukkitTask.isCancelled(); }

            @Override
            public Object get() { return null; }

            @Override
            public Object get(long timeout, TimeUnit unit) { return null; }
        };
    }

    @Override
    public void shutdown() {
        // У буккита нет ибо буккит гкод сделал (мы такое любим)
    }
}
