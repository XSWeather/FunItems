package dev.yooproject.funitems.item.animation.impl.item;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.item.animation.ItemParticle;
import dev.yooproject.funitems.services.IParticleService;
import dev.yooproject.nms.NMS;
import dev.yooproject.nms.NMSProvider;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class AuraParticle implements ItemParticle {

    @Override
    public void start(IParticleService service, Player player, Location location, boolean async) {

        ConfigurationSection cfg = FunItems.getInstance()
                .getConfigurationService()
                .getConfig(dev.yooproject.funitems.configuration.impl.ItemsConfiguration.class)
                .getConfig()
                .getConfigurationSection("items.aura.particles");

        if (cfg == null || location.getWorld() == null) return;

        double radius = cfg.getDouble("radius", 8.0);
        float velocity = (float) (cfg.getDouble("velocity", 30.0) / 1000.0);
        double boostY = cfg.getDouble("boost-y", 0.05);

        Particle mainParticle = Particle.valueOf(cfg.getString("type", "FIREWORKS_SPARK"));

        ConfigurationSection second = cfg.getConfigurationSection("second");
        Particle secondParticle = Particle.valueOf(second.getString("type", "VILLAGER_HAPPY"));

        int secondCount = second.getInt("count", 100);
        double secondBoostY = second.getDouble("boost-y", 1.0);
        double offsetX = second.getDouble("offset-x", 0.7);
        double offsetY = second.getDouble("offset-y", 1.0);
        double offsetZ = second.getDouble("offset-z", 0.7);

        NMS nms = NMSProvider.get();

        Runnable task = () -> {

            double baseX = location.getX();
            double baseY = location.getY() + boostY;
            double baseZ = location.getZ();

            for (double t = 0; t <= Math.PI * 2 * radius; t += 0.5) {

                float x = (float) (radius * Math.cos(t));
                float z = (float) (radius * Math.sin(t));

                for (Player p : location.getWorld().getPlayers()) {
                    nms.sendParticle(p, mainParticle, new Location(location.getWorld(), baseX, baseY, baseZ), x, 0.0f, z, velocity);
                }
            }

            Location secondLoc = location.clone().add(0, secondBoostY, 0);

            secondLoc.getWorld().spawnParticle(secondParticle, secondLoc, secondCount, offsetX, offsetY, offsetZ, 0.0);
        };

        if (async) {
            FunItems.getInstance().getAsyncExecutor().runAsync(task);
        } else {
            task.run();
        }
    }
}
