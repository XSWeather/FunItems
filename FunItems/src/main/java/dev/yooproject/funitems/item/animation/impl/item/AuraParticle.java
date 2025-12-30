package dev.yooproject.funitems.item.animation.impl.item;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.item.animation.ItemParticle;
import dev.yooproject.nms.NMS;
import dev.yooproject.nms.NMSProvider;
import dev.yooproject.funitems.services.IParticleService;
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

        if (cfg == null) return;

        double radius = cfg.getDouble("radius", 8.0);
        float velocity = (float) (cfg.getDouble("velocity", 30) / 1000.0);
        double boostY = cfg.getDouble("boost-y", 1.0);
        Particle mainParticle = Particle.valueOf(cfg.getString("type", "FIREWORKS_SPARK"));

        ConfigurationSection second = cfg.getConfigurationSection("second");
        Particle secondParticle = Particle.valueOf(second.getString("type", "VILLAGER_HAPPY"));
        int secondCount = second.getInt("count", 100);
        double secondBoostY = second.getDouble("boost-y", 1);
        double spread = second.getDouble("offset-x", 0.7);

        NMS nms = NMSProvider.get();

        Runnable task = () -> {
            for (double t = 0; t <= Math.PI * 2 * radius; t += 0.5) {
                float x = (float) (radius * Math.cos(t));
                float z = (float) (radius * Math.sin(t));

                for (Player p : location.getWorld().getPlayers()) {
                    nms.sendParticle(p, mainParticle, location.clone().add(0, boostY, 0), x, 0.0f, z, velocity);
                }
            }

            Location loc = location.clone().add(0, secondBoostY, 0);
            for (int i = 0; i < secondCount; i++) {
                float vx = (float) ((Math.random() - 0.5) * 2 * spread);
                float vy = (float) ((Math.random() - 0.5) * 2 * spread);
                float vz = (float) ((Math.random() - 0.5) * 2 * spread);

                float v = 0.2f + (float) Math.random() * 0.2f;

                for (Player p : location.getWorld().getPlayers()) {
                    nms.sendParticle(p, secondParticle, loc, vx, vy, vz, v);
                }
            }
        };

        if (async) {
            FunItems.getInstance().getAsyncExecutor().runAsync(task);
        } else {
            task.run();
        }
    }
}
