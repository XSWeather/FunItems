package dev.yooproject.funitems.item.handler;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.services.ItemCooldownService;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Map;

public class PlayerHandler implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        FunItems plugin = FunItems.getInstance();
        Player player = event.getPlayer();
        ItemCooldownService itemCooldownService = plugin.getItemCooldownService();

        Map<String, Long> cooldowns = itemCooldownService.getCooldowns(player);

        if (cooldowns != null && !cooldowns.isEmpty()) {
            DebugUtil.log("PlayerHandler", "[QUIT] Player [" + player.getName() + "] has active cooldowns: " + cooldowns.keySet());
            for (String key : new ArrayList<>(cooldowns.keySet())) {
                itemCooldownService.resetCooldown(player, key);
                DebugUtil.log("PlayerHandler", "[RESET] Cooldown [" + key + "] cleared for player [" + player.getName() + "]");
            }
        } else {
            DebugUtil.log("PlayerHandler", "[QUIT] Player [" + player.getName() + "] has no active cooldowns.");
        }
    }
}
