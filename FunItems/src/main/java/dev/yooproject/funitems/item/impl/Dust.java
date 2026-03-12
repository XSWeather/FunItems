package dev.yooproject.funitems.item.impl;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.item.IItem;
import dev.yooproject.funitems.item.Type;
import dev.yooproject.funitems.item.animation.IParticleType;
import dev.yooproject.funitems.item.events.ItemSpawnEvent;
import dev.yooproject.funitems.item.events.PlayerInteractItemEvent;
import dev.yooproject.funitems.providers.impl.ItemsProvider;
import dev.yooproject.funitems.providers.impl.LanguageProvider;
import dev.yooproject.funitems.services.EffectService;
import dev.yooproject.funitems.services.ItemCooldownService;
import dev.yooproject.funitems.services.RadiusService;
import dev.yooproject.funitems.services.WorldGuardFlagService;
import dev.yooproject.funitems.util.AntirelogUtil;
import dev.yooproject.funitems.util.ClanUtil;
import dev.yooproject.funitems.util.ColorUtil;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class Dust implements IItem<PlayerInteractEvent> {
    private final ItemStack item;
    private final Player caster;

    public Dust(ItemStack item, Player caster){
        this.item = item;
        this.caster = caster;
    }

    @Override
    public String getName() {
        return "dust";
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public Player getCaster() {
        return caster;
    }

    @Override
    public void use(Player player, PlayerInteractEvent event) {
        ItemCooldownService cooldownService = FunItems.getInstance().getItemCooldownService();
        LanguageProvider languageProvider = FunItems.getInstance()
                .getInitializationService()
                .getProviderService()
                .get(LanguageProvider.class);
        ItemsProvider itemsProvider = FunItems.getInstance()
                .getInitializationService()
                .getProviderService()
                .get(ItemsProvider.class);

        PlayerInteractItemEvent interactItemEvent = new PlayerInteractItemEvent(Type.DUST, player, item, event);
        Bukkit.getServer().getPluginManager().callEvent(interactItemEvent);

        if (!FunItems.getInstance().getConfig().getBoolean("settings.items.dust.enable")){
            interactItemEvent.setCancelled(true);
            player.sendMessage(ColorUtil.translate(languageProvider.get("items.item-disabled")));
        }

        WorldGuardFlagService wg = FunItems.getWorldGuardFlagService();
        if (wg.isEnabled() && !wg.isItemsAllowed(player)) {
            interactItemEvent.setCancelled(true);
            player.sendMessage(ColorUtil.translate(languageProvider.get("items.here-disabled")));
        }

        if (cooldownService.hasCooldown(player, "dust")) {
            int remaining = (int) cooldownService.getRemaining(player, "dust");
            player.sendMessage(ColorUtil.translate(languageProvider.get("items.cooldown").replace("{time}", String.valueOf(remaining))));
            return;
        } else if (cooldownService.hasCooldown(player, "dust_s")) {
            return;
        }

        if (interactItemEvent.isCancelled()) return;

        Map<String, Object> item = itemsProvider.get("dust");
        if (item == null) return;

        double radius = 10.0;
        Object radiusObj = item.get("radius");
        if (radiusObj instanceof Number) radius = ((Number) radiusObj).doubleValue();
        RadiusService radiusService = new RadiusService(radius);

        Sound sound = Sound.valueOf((String) item.getOrDefault("sound.name", "ENTITY_EXPERIENCE_ORB_PICKUP"));
        float volume = ((Number) item.getOrDefault("sound.volume", 1.0)).floatValue();
        float pitch = ((Number) item.getOrDefault("sound.pitch", 1.0)).floatValue();

        player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
        FunItems.getInstance().getIParticleService().play(player, IParticleType.DUST, player.getLocation());

        DebugUtil.log("Dust", "[INFO] Player [" + player.getName() + "] activated Dust [Radius: " + radius + "]");

        boolean hasTargets = false;

        for (Player target : player.getWorld().getPlayers()) {
            if (target.equals(player)) continue;
            if (target.getGameMode() == GameMode.SPECTATOR || target.getGameMode() == GameMode.CREATIVE) continue;
            if (wg.isEnabled() && !wg.isItemsAllowed(target)) continue;
            if (ClanUtil.is(caster, target) && !ClanUtil.pvp(caster)) continue;

            double multiplier = radiusService.getMultiplier(player.getLocation(), target);
            if (multiplier <= 0) continue;

            hasTargets = true;

            AntirelogUtil.startPvp(caster, false);

            AntirelogUtil.startPvp(target, false);

            DebugUtil.log("Dust", "[TARGET] Player [" + target.getName() + "] within range [Multiplier: " + String.format("%.2f", multiplier) + "]");

            Object giveEffectsObj = item.get("effects.give");
            if (giveEffectsObj instanceof Iterable<?> giveEffects) {
                for (Object obj : giveEffects) {
                    String effectStr = obj.toString();
                    String[] parts = effectStr.split(":");
                    PotionEffectType type = PotionEffectType.getByName(parts[0]);
                    if (type == null) {
                        DebugUtil.log("Dust", "[EFFECT] Unknown effect type: " + parts[0]);
                        continue;
                    }
                    int duration = Integer.parseInt(parts[1]) * 20;
                    int amplifier = Integer.parseInt(parts[2]);

                    EffectService.apply(target, type, duration, amplifier, multiplier);
                    DebugUtil.log("Dust", "[EFFECT] Applied [" + type + "] to [" + target.getName() + "] [Duration: " + (duration/20) + "s, Amplifier: " + amplifier + "]");
                }
            }

            Object removeEffectsObj = item.get("effects.remove");
            if (removeEffectsObj instanceof Iterable<?> removeEffects) {
                for (Object obj : removeEffects) {
                    String effectStr = obj.toString();
                    String[] parts = effectStr.split(":");
                    PotionEffectType type = PotionEffectType.getByName(parts[0]);
                    if (type == null) {
                        DebugUtil.log("Dust", "[EFFECT] Unknown effect type: " + parts[0]);
                        continue;
                    }
                    int durationTicks = (int) (Integer.parseInt(parts[1]) * 20 * multiplier);

                    DebugUtil.log("Dust", "[REMOVE EFFECT] Removing effect [" + type.getName() + "] from [" + target.getName() + "] for " + (durationTicks / 20.0) + "s (multiplier: " + String.format("%.2f", multiplier) + ")");
                    EffectService.remove(target, type, durationTicks);
                    DebugUtil.log("Dust", "[REMOVE EFFECT] Scheduled reapply of [" + type.getName() + "] to [" + target.getName() + "] after " + (durationTicks / 20.0) + "s");
                }
            }
        }

        if (hasTargets) {
            int cooldownTime = ((Number) item.getOrDefault("cooldown.time", 30)).intValue();
            cooldownService.setCooldown(player, "dust", cooldownTime);
            cooldownService.setBukkitCooldown(getItem().getType(), player, cooldownTime * 20);
            DebugUtil.log("Dust", "[COOLDOWN] Set [dust] for Player [" + player.getName() + "] [Time: " + cooldownTime + "s]");
        } else if ((boolean) item.getOrDefault("cooldown.nearby.enable", false) && !AntirelogUtil.isInPvP(player)) {
            double nearbyCooldown = ((Number) item.getOrDefault("cooldown.nearby.time", 10)).doubleValue();
            cooldownService.setCooldown(player, "dust_s", nearbyCooldown);
            cooldownService.setBukkitCooldown(getItem().getType(), player, (int) (20 * nearbyCooldown));
            DebugUtil.log("Dust", "[COOLDOWN] Set [dust_s] for Player [" + player.getName() + "] [Time: " + nearbyCooldown + "s]");
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        int amount = handItem.getAmount();
        if (amount <= 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            handItem.setAmount(amount - 1);
        }

        Bukkit.getServer().getPluginManager().callEvent(new ItemSpawnEvent(handItem, player, player.getLocation(), radiusService));
    }
}
