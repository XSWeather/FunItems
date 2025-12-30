package dev.yooproject.funitems.item.impl;

import dev.yooproject.funitems.item.IItem;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Plast implements IItem<PlayerInteractEvent> {
    private Player caster;
    private ItemStack item;
    public Plast(Player caster, ItemStack itemStack){
        this.caster = caster;
        this.item = itemStack;
    }

    @Override
    public String getName() {
        return "plast";
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

    }
}
