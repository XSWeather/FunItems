package dev.yooproject.funitems.configuration;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Базовый класс для кфг
 */
public abstract class AbstractConfiguration implements Configuration {

    private final File file;
    private FileConfiguration config;

    public AbstractConfiguration(String fileName) {
        this.file = new File(FunItems.getInstance().getDataFolder(), fileName);
        createFile();
        loadConfig();
    }

    private void createFile() {
        if (!file.exists()) {
            try {
                FunItems.getInstance().saveResource(file.getName(), false);
            } catch (IllegalArgumentException ignored) {
                try {
                    if (!file.createNewFile()) {
                        FunItems.getInstance().getLogger().warning("Failed to create file: " + file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    public void reload() {
        try {
            config = YamlConfiguration.loadConfiguration(file);
            DebugUtil.log("AbstractConfiguration", file.getName() + " reloaded");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получить путь к кфг
     */
    public File getFile() {
        return file;
    }
}
