package dev.yooproject.funitems.item.impl;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.item.IItem;
import dev.yooproject.funitems.item.Type;
import dev.yooproject.funitems.item.animation.IParticleType;
import dev.yooproject.funitems.item.events.PlayerInteractItemEvent;
import dev.yooproject.funitems.providers.impl.ItemsProvider;
import dev.yooproject.funitems.providers.impl.LanguageProvider;
import dev.yooproject.funitems.services.*;
import dev.yooproject.funitems.util.ColorUtil;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Snowball implements IItem<PlayerInteractEvent>, Listener {
    private final ItemStack item;
    private final Player caster;
    private final Map<UUID, Long> frozenPlayers = new ConcurrentHashMap<>();

    public Snowball(ItemStack item, Player caster){
        this.item = item;
        this.caster = caster;
        FunItems.getInstance().getServer().getPluginManager().registerEvents(this, FunItems.getInstance());
    }

    @Override
    public String getName() {
        return "snowball";
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

        PlayerInteractItemEvent interactItemEvent = new PlayerInteractItemEvent(Type.SNOWBALL, player, item, event);
        Bukkit.getServer().getPluginManager().callEvent(interactItemEvent);

        if (!FunItems.getInstance().getConfig().getBoolean("settings.items.snowball.enable")){
            interactItemEvent.setCancelled(true);
            player.sendMessage(ColorUtil.translate(languageProvider.get("items.item-disabled")));
        }

        WorldGuardFlagService wg = FunItems.getWorldGuardFlagService();
        if (wg.isEnabled() && !wg.isItemsAllowed(player)) {
            interactItemEvent.setCancelled(true);
            player.sendMessage(ColorUtil.translate(languageProvider.get("items.here-disabled")));
        }

        if (cooldownService.hasCooldown(player, "snowball")) {
            int remaining = (int) cooldownService.getRemaining(player, "snowball");
            player.sendMessage(ColorUtil.translate(languageProvider.get("items.cooldown").replace("{time}", String.valueOf(remaining))));
            return;
        }

        if (interactItemEvent.isCancelled()) return;

        Map<String, Object> item = itemsProvider.get("snowball");
        int cooldownTime = ((Number) item.getOrDefault("cooldown.time", 30)).intValue();
        cooldownService.setCooldown(player, "snowball", cooldownTime);
        cooldownService.setBukkitCooldown(getItem().getType(), player, cooldownTime * 20);
        DebugUtil.log("Snowball", "[COOLDOWN] Set [snowball] for Player [" + player.getName() + "] [Time: " + cooldownTime + "s]");
        org.bukkit.entity.Snowball snowball = player.launchProjectile(org.bukkit.entity.Snowball.class);
        NamespacedKey key = new NamespacedKey(FunItems.getInstance(), "snowball");
        snowball.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof org.bukkit.entity.Snowball snowball)) return;

        NamespacedKey key = new NamespacedKey(FunItems.getInstance(), "snowball");
        if (!snowball.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;

        Location hitLocation;
        if (event.getHitBlock() != null) {
            hitLocation = event.getHitBlock().getLocation().add(0.5, 0.5, 0.5);
        } else if (event.getHitEntity() != null) {
            hitLocation = event.getHitEntity().getLocation().add(0, 1, 0);
        } else {
            hitLocation = snowball.getLocation();
        }

        if (!(snowball.getShooter() instanceof Player shooter)) return;

        IParticleService iParticleService = FunItems.getInstance().getIParticleService();
        iParticleService.play(shooter, IParticleType.SNOWBALL, hitLocation);

        ItemsProvider itemsProvider = FunItems.getInstance().getInitializationService().getProviderService().get(ItemsProvider.class);
        Map<String, Object> itemConfig = itemsProvider.get("snowball");
        double radius = 7.0;
        Object radiusObj = itemConfig.get("radius");
        if (radiusObj instanceof Number) radius = ((Number) radiusObj).doubleValue();
        RadiusService radiusService = new RadiusService(radius);

        String soundName = (String) itemConfig.getOrDefault("sound.name", "ENTITY_EXPERIENCE_ORB_PICKUP");
        float volume = ((Number) itemConfig.getOrDefault("sound.volume", 1.0)).floatValue();
        float pitch = ((Number) itemConfig.getOrDefault("sound.pitch", 1.0)).floatValue();

        try {
            org.bukkit.Sound sound = org.bukkit.Sound.valueOf(soundName);
            for (Player target : shooter.getWorld().getPlayers()) {
                if (radiusService.getMultiplier(hitLocation, target) > 0) {
                    target.playSound(hitLocation, sound, volume, pitch);
                }
            }
        } catch (IllegalArgumentException ignored) {}

        long baseFreeze = ((Number) itemConfig.getOrDefault("freeze.time", 3)).longValue() * 20;

        for (Player target : shooter.getWorld().getPlayers()) {
            double multiplier = radiusService.getMultiplier(hitLocation, target);
            if (multiplier <= 0) continue;

            long freezeTicks = (long) (baseFreeze * multiplier);
            frozenPlayers.put(target.getUniqueId(), System.currentTimeMillis() + freezeTicks * 50);

            for (String effectStr : (Iterable<String>) itemConfig.getOrDefault("effects.give", java.util.Collections.emptyList())) {
                String[] parts = effectStr.split(":");
                PotionEffectType type = PotionEffectType.getByName(parts[0]);
                int amplifier = Integer.parseInt(parts[1]);
                int duration = Integer.parseInt(parts[2]);
                EffectService.apply(target, type, duration, amplifier, multiplier);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!frozenPlayers.containsKey(uuid)) return;

        long endTime = frozenPlayers.get(uuid);
        if (System.currentTimeMillis() >= endTime) {
            frozenPlayers.remove(uuid);
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        double squaredDistance = from.distanceSquared(to);
        double distanceThreshold = 0.01D;
        if (squaredDistance < distanceThreshold) {
            return;
        }
        event.setCancelled(true);
    }
}
