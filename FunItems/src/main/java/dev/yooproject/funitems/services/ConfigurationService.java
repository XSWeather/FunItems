package dev.yooproject.funitems.services;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.configuration.AbstractConfiguration;
import dev.yooproject.funitems.configuration.folder.Folder;
import dev.yooproject.funitems.configuration.folder.AbstractFolder;
import dev.yooproject.funitems.configuration.folder.impl.GuisFolder;
import dev.yooproject.funitems.configuration.folder.impl.SchematicsFolder;
import dev.yooproject.funitems.configuration.impl.ItemsConfiguration;
import dev.yooproject.funitems.configuration.impl.LanguageConfiguration;
import dev.yooproject.funitems.configuration.impl.TrapsConfiguration;
import dev.yooproject.funitems.util.DebugUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер всех кфг и папок
 */
public class ConfigurationService {

    private final Map<Class<? extends AbstractConfiguration>, AbstractConfiguration> configurations = new HashMap<>();

    private final Map<Class<? extends AbstractFolder>, AbstractFolder> folders = new HashMap<>();

    private ProviderService providerService;

    public ConfigurationService() {}

    public ConfigurationService(ProviderService providerService) {
        this.providerService = providerService;
    }

    public void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }

    /**
     * Инициализация и регистрация всех кфг и папок
     */
    public void registerAll() {
        registerFolder(new GuisFolder());
        registerFolder(new SchematicsFolder());
        registerConfig(new ItemsConfiguration());
        registerConfig(new LanguageConfiguration());
        registerConfig(new TrapsConfiguration());
    }

    /**
     * Зарегистрировать папку
     */
    public <T extends AbstractFolder> void registerFolder(T folder) {
        folder.create();
        folders.put((Class<T>) folder.getClass(), folder);
    }

    /**
     * Получить папку
     */
    public <T extends AbstractFolder> T getFolder(Class<T> folderClass) {
        return (T) folders.get(folderClass);
    }

    /**
     * Зарегистрировать кфг
     */
    public <T extends AbstractConfiguration> void registerConfig(T config) {
        configurations.put((Class<T>) config.getClass(), config);
    }

    /**
     * Получить кфг
     */
    public <T extends AbstractConfiguration> T getConfig(Class<T> configClass) {
        return (T) configurations.get(configClass);
    }

    /**
     * Перезагрузить кфг и провайдер
     */
    public <T extends AbstractConfiguration> void reloadConfig(Class<T> configClass) {
        AbstractConfiguration config = configurations.get(configClass);
        if (config != null) {
            if (providerService != null) {
                providerService.reloadAll();
            }
        } else {
            DebugUtil.log("ConfigurationService", "Config " + configClass.getSimpleName() + " not found!");
        }
    }

    /**
     * Перезагрузить все кфг и провайдеры
     */
    public void reloadAllConfigs() {
        configurations.values().forEach(AbstractConfiguration::reload);
        if (providerService != null) {
            providerService.reloadAll();
        }
    }

    /**
     * Сохранить все кфг
     */
    public void saveAllConfigs() {
        configurations.values().forEach(AbstractConfiguration::save);
    }
}
