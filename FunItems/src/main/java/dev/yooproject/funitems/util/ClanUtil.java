package dev.yooproject.funitems.util;

import me.arial.api.ClansAPI;
import me.arial.api.clan.Clan;
import me.arial.api.player.ClanPlayer;
import org.bukkit.entity.Player;

public class ClanUtil {
    public static boolean is(Player p1, Player p2) {
        ClanPlayer cp1 = ClansAPI.getPlayerManager().getPlayer(p1);
        ClanPlayer cp2 = ClansAPI.getPlayerManager().getPlayer(p2);
        if (cp1 != null && cp2 != null) {
            return cp1.hasClan() && cp2.hasClan() ? cp1.getClanId().equals(cp2.getClanId()) : false;
        } else {
            return false;
        }
    }

    public static boolean pvp(Player player) {
        ClanPlayer cp = ClansAPI.getPlayerManager().getPlayer(player);
        if (cp != null && cp.hasClan()) {
            Clan clan = cp.getClan();
            return clan == null ? false : clan.isAllyDamage();
        } else {
            return false;
        }
    }
}
