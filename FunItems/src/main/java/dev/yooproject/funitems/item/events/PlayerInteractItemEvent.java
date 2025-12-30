package dev.yooproject.funitems.item.events;

import dev.yooproject.funitems.item.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractItemEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Type type;
    private final Player player;
    private final ItemStack item;
    private final PlayerInteractEvent bukkitEvent;

    private boolean cancelled = false;

    public PlayerInteractItemEvent(Type type, Player player, ItemStack item, PlayerInteractEvent bukkitEvent) {
        this.type = type;
        this.player = player;
        this.item = item;
        this.bukkitEvent = bukkitEvent;
    }

    public Type getType() {
        return type;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public PlayerInteractEvent getBukkitEvent() {
        return bukkitEvent;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
