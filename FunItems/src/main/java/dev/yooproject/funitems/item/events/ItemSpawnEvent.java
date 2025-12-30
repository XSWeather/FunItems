package dev.yooproject.funitems.item.events;

import dev.yooproject.funitems.services.RadiusService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemSpawnEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ItemStack item;
    private final Player player;
    private final Location source;
    private final double radius;
    private final RadiusService radiusService;

    public ItemSpawnEvent(ItemStack item, Player player, Location source, RadiusService radiusService) {
        this.item = item;
        this.player = player;
        this.source = source;
        this.radiusService = radiusService;
        this.radius = radiusService.getMaxRadius();
    }

    public ItemStack getItem() {
        return item;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getSource() {
        return source;
    }

    public double getRadius() {
        return radius;
    }

    public double getMultiplier(Player target) {
        return radiusService.getMultiplier(source, target);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
