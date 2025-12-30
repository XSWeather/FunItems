package dev.yooproject.funitems.providers.impl;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.configuration.impl.ItemsConfiguration;
import dev.yooproject.funitems.providers.IProvider;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ItemsProvider implements IProvider {

    private final Map<String, Map<String, Object>> items = new HashMap<>();

    /**
     * Загрузить предметы из items.yml
     */
    public void load() {
        DebugUtil.log("ItemsProvider", "Loading items...");

        items.clear();

        try {
            ItemsConfiguration itemsConfig = FunItems.getInstance()
                    .getConfigurationService()
                    .getConfig(ItemsConfiguration.class);

            if (itemsConfig == null) {
                DebugUtil.error("ItemsProvider", "ItemsConfiguration is NULL!");
                return;
            }

            FileConfiguration cfg = itemsConfig.getConfig();

            for (String key : cfg.getConfigurationSection("items").getKeys(false)) {
                Map<String, Object> section = cfg.getConfigurationSection("items." + key).getValues(true);
                items.put(key, section);
            }

            DebugUtil.log("ItemsProvider", "Loaded " + items.size() + " items");

        } catch (Exception e) {
            DebugUtil.exception("ItemsProvider", e);
        }
    }

    /**
     * Перезагрузить
     */
    @Override
    public void reload() {
        load();
    }

    /**
     * Получить данные предмета по ключу
     */
    public Map<String, Object> get(String key) {
        return items.get(key);
    }

    /**
     * Проверка есть ли предмет
     */
    public boolean contains(String key) {
        return items.containsKey(key);
    }

    /**
     * Вернуть все предметы
     */
    public Map<String, Map<String, Object>> getAll() {
        return items;
    }
}
