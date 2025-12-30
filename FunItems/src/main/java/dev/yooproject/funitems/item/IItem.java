package dev.yooproject.funitems.item;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public interface IItem<E extends Event> {
    String getName();
    ItemStack getItem();
    Player getCaster();
    void use(Player player, E event);
}