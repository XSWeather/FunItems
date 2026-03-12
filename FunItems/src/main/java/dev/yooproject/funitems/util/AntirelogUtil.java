package dev.yooproject.funitems.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class AntirelogUtil {

    private static Plugin getAntiRelog() {
        return Bukkit.getPluginManager().getPlugin("AntiRelog");
    }

    private static Object getPvpManager(Object antirelog) throws Exception {
        Method method = antirelog.getClass().getMethod("getPvpManager");
        return method.invoke(antirelog);
    }

    public static void startPvp(Player player, boolean bypass) {
        try {
            if (player.isOp() || player.hasPermission("*")) return;

            Plugin antirelog = getAntiRelog();
            if (antirelog == null) return;

            Object manager = getPvpManager(antirelog);

            Method method = manager.getClass()
                    .getDeclaredMethod("startPvp", Player.class, boolean.class, boolean.class);

            method.setAccessible(true);
            method.invoke(manager, player, bypass, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopPvp(Player player) {
        try {
            Plugin antirelog = getAntiRelog();
            if (antirelog == null) return;

            Object manager = getPvpManager(antirelog);

            Method method = manager.getClass()
                    .getDeclaredMethod("stopPvPSilent", Player.class);

            method.setAccessible(true);
            method.invoke(manager, player);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isInPvP(Player player) {
        try {
            Plugin antirelog = getAntiRelog();
            if (antirelog == null) return false;

            Object manager = getPvpManager(antirelog);

            Method method = manager.getClass()
                    .getDeclaredMethod("isInPvP", Player.class);

            method.setAccessible(true);
            return (boolean) method.invoke(manager, player);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
