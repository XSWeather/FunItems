package dev.yooproject.funitems.services;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardFlagService {

    public static StateFlag FUNITEMS_ITEMS_ENABLE;
    public static StateFlag FUNITEMS_TRAPS_ENABLE;

    private boolean enabled;

    public void registerFlags() {
        if (!isWorldGuardPresent()) {
            DebugUtil.log("WorldGuardFlagService", "WorldGuard not found, flags disabled");
            enabled = false;
            return;
        }

        try {
            var registry = WorldGuard.getInstance().getFlagRegistry();

            FUNITEMS_ITEMS_ENABLE = new StateFlag("funitems-items-enable", true);

            FUNITEMS_TRAPS_ENABLE = new StateFlag("funitems-traps-enable", true);

            registry.register(FUNITEMS_ITEMS_ENABLE);
            registry.register(FUNITEMS_TRAPS_ENABLE);

            enabled = true;
            DebugUtil.log("WorldGuardFlagService", "Custom WorldGuard flags registered");

        } catch (Exception e) {
            enabled = false;
            DebugUtil.exception("WorldGuardFlagService", e);
        }
    }

    public boolean isItemsAllowed(Location location, Player player) {
        if (!enabled || FUNITEMS_ITEMS_ENABLE == null) return true;

        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();

            ApplicableRegionSet regions =
                    query.getApplicableRegions(BukkitAdapter.adapt(location));

            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            StateFlag.State state = regions.queryState(localPlayer, FUNITEMS_ITEMS_ENABLE);

            if (state == null) {
                return true;
            }

            return state == StateFlag.State.ALLOW;

        } catch (Exception e) {
            DebugUtil.exception("WorldGuardFlagService", e);
            return true;
        }
    }

    public boolean isItemsAllowed(Player player) {
        return isAllowed(player, FUNITEMS_ITEMS_ENABLE, true);
    }

    public boolean isTrapsAllowed(Player player) {
        return isAllowed(player, FUNITEMS_TRAPS_ENABLE, true);
    }

    private boolean isAllowed(Player player, StateFlag flag, boolean def) {
        if (!enabled || flag == null) return def;

        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

            RegionQuery query = container.createQuery();
            ApplicableRegionSet regions = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));

            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

            StateFlag.State state = regions.queryState(localPlayer, flag);

            if (state == null) {
                return def;
            }

            return state == StateFlag.State.ALLOW;

        } catch (Exception e) {
            DebugUtil.exception("WorldGuardFlagService", e);
            return def;
        }
    }

    private boolean isWorldGuardPresent() {
        return Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
