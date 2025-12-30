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
import dev.yooproject.funitems.services.IParticleService;
import dev.yooproject.funitems.services.ItemCooldownService;
import dev.yooproject.funitems.services.RadiusService;
import dev.yooproject.funitems.util.ClanUtil;
import dev.yooproject.funitems.util.ColorUtil;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Aura implements IItem<PlayerInteractEvent> {

    private static final Set<PotionEffectType> NEGATIVE_EFFECTS = Set.of(
            PotionEffectType.BLINDNESS,
            PotionEffectType.CONFUSION,
            PotionEffectType.POISON,
            PotionEffectType.WITHER,
            PotionEffectType.SLOW,
            PotionEffectType.SLOW_DIGGING,
            PotionEffectType.WEAKNESS,
            PotionEffectType.HUNGER,
            PotionEffectType.BAD_OMEN,
            PotionEffectType.LEVITATION
    );

    private final ItemStack item;
    private final Player caster;

    public Aura(ItemStack item, Player caster) {
        this.item = item;
        this.caster = caster;
    }

    @Override
    public String getName() {
        return "aura";
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
        LanguageProvider lang = FunItems.getInstance()
                .getInitializationService()
                .getProviderService()
                .get(LanguageProvider.class);
        ItemsProvider itemsProvider = FunItems.getInstance()
                .getInitializationService()
                .getProviderService()
                .get(ItemsProvider.class);

        PlayerInteractItemEvent interactEvent = new PlayerInteractItemEvent(Type.AURA, player, item, event);
        Bukkit.getPluginManager().callEvent(interactEvent);

        if (cooldownService.hasCooldown(player, "aura")) {
            int remaining = (int) cooldownService.getRemaining(player, "aura");
            player.sendMessage(ColorUtil.translate(lang.get("items.cooldown").replace("{time}", String.valueOf(remaining))));
            return;
        }

        if (interactEvent.isCancelled()) return;

        Map<String, Object> cfg = itemsProvider.get("aura");
        if (cfg == null) return;

        double radius = ((Number) cfg.getOrDefault("radius", 2)).doubleValue();
        RadiusService radiusService = new RadiusService(radius);

        Sound sound = Sound.valueOf((String) cfg.getOrDefault("sound.name", "BLOCK_BEACON_ACTIVATE"));

        player.getWorld().playSound(player.getLocation(), sound, ((Number) cfg.getOrDefault("sound.volume", 1.0)).floatValue(), ((Number) cfg.getOrDefault("sound.pitch", 1.0)).floatValue());

        IParticleService particleService = FunItems.getInstance().getIParticleService();
        particleService.play(player, IParticleType.AURA, player.getLocation());

        DebugUtil.log("Aura", "[INFO] Player [" + player.getName() + "] activated Aura [Radius: " + radius + "]");

        boolean applied = false;

        for (Player target : player.getWorld().getPlayers()) {
            if (target.getGameMode() == GameMode.SPECTATOR) continue;

            if (!target.equals(player)) {
                if (!ClanUtil.is(player, target)) continue;
                if (ClanUtil.pvp(player)) continue;
            }

            double multiplier = radiusService.getMultiplier(player.getLocation(), target);
            if (multiplier <= 0) continue;

            applied = true;
            Object effectsObj = cfg.get("effects");
            if (effectsObj instanceof org.bukkit.configuration.ConfigurationSection section) {
                Map<String, Object> effects = section.getValues(false);

                if (effects.containsKey("remove")) {
                    for (String remove : (List<String>) effects.get("remove")) {
                        if (remove.equalsIgnoreCase("NEGATIVE")) {
                            for (PotionEffect pe : target.getActivePotionEffects()) {
                                if (NEGATIVE_EFFECTS.contains(pe.getType())) {
                                    target.removePotionEffect(pe.getType());
                                    DebugUtil.log("Aura", "[REMOVE] Removed negative effect [" + pe.getType().getName() + "] from player [" + target.getName() + "]");
                                }
                            }
                        } else if (remove.equalsIgnoreCase("ALL")) {
                            for (PotionEffect pe : target.getActivePotionEffects()) {
                                target.removePotionEffect(pe.getType());
                                DebugUtil.log("Aura", "[REMOVE] Removed effect [" + pe.getType().getName() + "] from player [" + target.getName() + "]");
                            }
                        } else {
                            PotionEffectType type = PotionEffectType.getByName(remove);
                            if (type != null) {
                                target.removePotionEffect(type);
                                DebugUtil.log("Aura", "[REMOVE] Removed effect [" + type.getName() + "] from player [" + target.getName() + "]");
                            }
                        }
                    }
                }

                if (effects.containsKey("give")) {
                    for (String effectStr : (List<String>) effects.get("give")) {
                        String[] parts = effectStr.split(";");
                        PotionEffectType type = PotionEffectType.getByName(parts[0]);
                        int amplifier = Integer.parseInt(parts[1]);
                        int duration = Integer.parseInt(parts[2]) * 20;
                        if (type != null) {
                            EffectService.apply(target, type, duration, amplifier, 1.0);
                        }
                    }
                }
            }

            DebugUtil.log("Aura", "[TARGET] Buffed player [" + target.getName() + "]");
        }

        if (applied) {
            int cd = ((Number) cfg.getOrDefault("cooldown.time", 30)).intValue();
            cooldownService.setCooldown(player, "aura", cd);
            cooldownService.setBukkitCooldown(getItem().getType(), player, cd * 20);
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        hand.setAmount(hand.getAmount() - 1);
        if (hand.getAmount() <= 0) {
            player.getInventory().setItemInMainHand(null);
        }

        Bukkit.getPluginManager().callEvent(new ItemSpawnEvent(hand, player, player.getLocation(), radiusService));
    }
}
