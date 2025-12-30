package dev.yooproject.funitems.configuration;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Интерфейс для работы с кфг
 */
public interface Configuration {

    /**
     * Получить кфг
     * @return FileConfiguration
     */
    FileConfiguration getConfig();

    /**
     * Сохранить кфг
     */
    void save();

    /**
     * Релоуднуть кфг
     */
    void reload();
}
