package dev.yooproject.funitems.services;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.async.AsyncExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class EffectService {

    /**
     * Применяет эффект к игроку с учетом множителя радиуса.
     */
    public static void apply(Player player, PotionEffectType type, int baseDuration, int amplifier, double multiplier) {
        if (multiplier <= 0) return;
        player.addPotionEffect(new PotionEffect(type, (int)(baseDuration * multiplier), amplifier));
    }

    /**
     * Временно убирает эффект с игрока на указанное время, после чего возвращает его обратно.
     *
     * @param player игрок
     * @param type тип эффекта
     * @param durationTicks длительность эффекта в тикcах
     */
    public static void remove(Player player, PotionEffectType type, int durationTicks) {
        PotionEffect active = player.getPotionEffect(type);

        if (active == null) return;
        int originalAmplifier = active.getAmplifier();
        int remainingTicks = active.getDuration();
        player.removePotionEffect(type);
        AsyncExecutor async = FunItems.getInstance().getAsyncExecutor();
        if (async != null) {
            long delayMillis = durationTicks * 50L;
            async.runAsync(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(delayMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                FunItems.getInstance().getServer().getScheduler().runTask(FunItems.getInstance(), () -> {
                    player.addPotionEffect(new PotionEffect(type, remainingTicks, originalAmplifier));
                });
            });
        }
    }
}
