package dev.yooproject.funitems.services;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemCooldownService {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    /**
     * Проверяет, есть ли кулдаун у игрока для конкретного ключа
     */
    public boolean hasCooldown(Player player, String key) {
        long now = System.currentTimeMillis();
        Map<String, Long> map = cooldowns.get(player.getUniqueId());

        if (map == null) return false;
        Long expires = map.get(key);

        if (expires == null) return false;
        if (expires <= now) {
            map.remove(key);
            return false;
        }
        return true;
    }

    /**
     * Возвращает оставшееся время в секундах
     */
    public long getRemaining(Player player, String key) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) return 0;

        Long expires = map.get(key);
        if (expires == null) return 0;

        long left = expires - System.currentTimeMillis();
        return Math.max(left / 1000, 0);
    }

    /**
     * Устанавливает кулдаун — принимает long, int и double.
     * Хранит всё в миллисекундах.
     */
    public void setCooldown(Player player, String key, double seconds) {
        long ms = (long) (seconds * 1000);
        cooldowns.computeIfAbsent(player.getUniqueId(), v -> new HashMap<>()).put(key, System.currentTimeMillis() + ms);
    }

    /**
     * Сбрасывает кулдаун
     */
    public void resetCooldown(Player player, String key) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map != null) map.remove(key);
    }

    /**
     * Получение всех кулдаунов
     */
    public Map<String, Long> getCooldowns(Player player) {
        return cooldowns.get(player.getUniqueId());
    }

    /**
     * Установить дефолт кулдаун от баккита
     */
    public void setBukkitCooldown(Material material, Player player, int ticks){
        player.setCooldown(material, ticks);
    }
}
