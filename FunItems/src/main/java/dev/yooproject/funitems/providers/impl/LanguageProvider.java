package dev.yooproject.funitems.providers.impl;

import dev.yooproject.funitems.configuration.impl.LanguageConfiguration;
import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.providers.IProvider;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class LanguageProvider implements IProvider {

    private final Map<String, String> messages = new HashMap<>();

    /**
     * Загрузить сообщения из language.yml
     */
    public void load() {
        DebugUtil.log("LanguageProvider", "Loading messages...");

        messages.clear();

        try {
            LanguageConfiguration language = FunItems.getInstance().getConfigurationService().getConfig(LanguageConfiguration.class);

            if (language == null) {
                DebugUtil.error("LanguageProvider", "LanguageConfiguration is NULL!");
                return;
            }

            FileConfiguration cfg = language.getConfig();

            for (String key : cfg.getKeys(true)) {
                if (!cfg.isString(key)) continue;
                messages.put(key, cfg.getString(key));
            }

            DebugUtil.log("LanguageProvider", "Loaded " + messages.size() + " messages");

        } catch (Exception e) {
            DebugUtil.exception("LanguageProvider", e);
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
     * Получить сообщение по ключу
     */
    public String get(String key) {
        return messages.getOrDefault(key, "Message '" + key + "' not found");
    }

    /**
     * Проверка есть ли ключ
     */
    public boolean contains(String key) {
        return messages.containsKey(key);
    }

    /**
     * Вернуть все ключи
     */
    public Map<String, String> getAll() {
        return messages;
    }
}
