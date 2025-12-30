package dev.yooproject.funitems.services;

import dev.yooproject.funitems.providers.IProvider;
import dev.yooproject.funitems.util.DebugUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ProviderService {

    private final Map<Class<?>, Object> providers = new HashMap<>();

    /**
     * Регистрация провайдера
     */
    public <T> void register(T provider) {
        providers.put(provider.getClass(), provider);
        DebugUtil.log("ProviderService", "Registered provider: " + provider.getClass().getSimpleName());
        try {
            Method load = provider.getClass().getMethod("load");
            load.invoke(provider);
            DebugUtil.log("ProviderService", provider.getClass().getSimpleName() + " loaded");
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            DebugUtil.exception("ProviderService", e);
        }
    }

    /**
     * Получить провайдер
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return (T) providers.get(clazz);
    }

    /**
     * Перезагрузка всех провайдеров
     */
    public void reloadAll() {
        DebugUtil.log("ProviderService", "Reloading all providers...");

        providers.values().forEach(provider -> {
            if (provider instanceof IProvider ip) {
                ip.reload();
                DebugUtil.log("ProviderService", provider.getClass().getSimpleName() + " reloaded (IProvider)");
            } else {
                try {
                    Method load = provider.getClass().getMethod("load");
                    load.invoke(provider);
                    DebugUtil.log("ProviderService", provider.getClass().getSimpleName() + " reloaded (reflection)");
                } catch (NoSuchMethodException ignored) {
                } catch (Exception e) {
                    DebugUtil.exception("ProviderService", e);
                }
            }
        });

        DebugUtil.log("ProviderService", "All providers reloaded!");
    }

    /**
     * Перезагрузка конкретного провайдера
     */
    public <T> void reload(Class<T> clazz) {
        Object provider = providers.get(clazz);
        if (provider == null) return;

        if (provider instanceof IProvider ip) {
            ip.reload();
            DebugUtil.log("ProviderService", clazz.getSimpleName() + " reloaded (IProvider)");
        } else {
            try {
                Method load = provider.getClass().getMethod("load");
                load.invoke(provider);
                DebugUtil.log("ProviderService", clazz.getSimpleName() + " reloaded (reflection)");
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                DebugUtil.exception("ProviderService", e);
            }
        }
    }
}
