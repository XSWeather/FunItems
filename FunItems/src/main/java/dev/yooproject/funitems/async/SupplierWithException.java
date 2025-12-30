package dev.yooproject.funitems.async;

@FunctionalInterface
public interface SupplierWithException<T> {
    T get() throws Exception;
}
