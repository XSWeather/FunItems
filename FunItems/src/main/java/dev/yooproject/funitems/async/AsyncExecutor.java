package dev.yooproject.funitems.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface AsyncExecutor {

    /**
     * Выполнить задачу асинхронно
     */
    CompletableFuture<Void> runAsync(Runnable task);

    /**
     * Выполнить задачу с возвратом значения
     */
    <T> CompletableFuture<T> runAsync(SupplierWithException<T> task);

    /**
     * Запустить задачу, которая повторяется с фиксированным периодом.
     *
     * @param task задача для выполнения
     * @param initialDelayMillis задержка перед первым запуском в миллисекундах
     * @param periodMillis период между запусками в миллисекундах
     * @return ScheduledFuture, с помощью которого можно отменять повторяющуюся задачу
     */
    ScheduledFuture<?> runRepeating(Runnable task, long initialDelayMillis, long periodMillis);

    /**
     * Остановить пул потоков
     */
    void shutdown();
}
