package dev.yooproject.funitems.item.handler;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.configuration.impl.ItemsConfiguration;
import dev.yooproject.funitems.item.IItem;
import dev.yooproject.funitems.item.Type;
import dev.yooproject.funitems.item.impl.Aura;
import dev.yooproject.funitems.item.impl.Disorientation;
import dev.yooproject.funitems.item.impl.Dust;
import dev.yooproject.funitems.item.impl.Snowball;
import dev.yooproject.funitems.services.ItemService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemHandler implements Listener {

    private final ItemService itemService;

    public ItemHandler() {
        ItemsConfiguration configuration = FunItems.getInstance().getConfigurationService().getConfig(ItemsConfiguration.class);
        this.itemService = new ItemService(configuration);
    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null) return;
        Type type = itemService.isItem(itemStack);
        if (type == null) return;

        event.setCancelled(true);
        switch (type) {
            case DISORIENTATION -> {
                IItem iItem = new Disorientation(itemStack, player);
                iItem.use(player, event);
            }
            case DUST -> {
                IItem iItem = new Dust(itemStack, player);
                iItem.use(player, event);
            }
            case SNOWBALL -> {
                IItem iItem = new Snowball(itemStack, player);
                iItem.use(player, event);
            }
            case AURA -> {
                IItem iItem = new Aura(itemStack, player);
                iItem.use(player, event);
            }
        }
    }
}
