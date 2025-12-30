package dev.yooproject.funitems.item.animation.impl.item;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.item.animation.ItemParticle;
import dev.yooproject.nms.NMS;
import dev.yooproject.nms.NMSProvider;
import dev.yooproject.funitems.services.IParticleService;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DustParticle implements ItemParticle {

    @Override
    public void start(IParticleService service, Player player, Location location, boolean async) {

        var config = FunItems.getInstance()
                .getConfigurationService()
                .getConfig(dev.yooproject.funitems.configuration.impl.ItemsConfiguration.class)
                .getConfig()
                .getConfigurationSection("items.dust.particles");

        if (config == null) return;

        final double radius1 = config.getDouble("radius");
        final int count1 = config.getInt("count");
        final float speed1 = (float) (config.getDouble("velocity") / 1000.0);
        final double interval1 = config.getDouble("interval");
        final Particle particle1 = Particle.valueOf(config.getString("type"));
        final float height1 = 0.6f;

        var second = config.getConfigurationSection("second");

        final double radius2 = second != null ? second.getDouble("radius") : 0;
        final int count2 = second != null ? second.getInt("count") : 0;
        final float speed2 = second != null ? (float) (second.getDouble("velocity") / 1000.0) : 0f;
        final double interval2 = second != null ? second.getDouble("interval") : 1;
        final Particle particle2 = second != null ? Particle.valueOf(second.getString("type")) : null;

        final NMS nms = NMSProvider.get();

        Runnable task = () -> {

            for (int i = 0; i < count1; i++) {
                double angle = 2 * Math.PI * i / count1 * interval1;
                float x = (float) (radius1 * Math.cos(angle));
                float z = (float) (radius1 * Math.sin(angle));

                for (Player p : location.getWorld().getPlayers()) {
                    nms.sendParticle(p, particle1, location, x, height1, z, speed1);
                }
            }

            if (particle2 != null) {
                for (int i = 0; i < count2; i++) {
                    double angle = 2 * Math.PI * i / count2 * interval2;
                    float x = (float) (radius2 * Math.cos(angle));
                    float z = (float) (radius2 * Math.sin(angle));

                    for (Player p : location.getWorld().getPlayers()) {
                        nms.sendParticle(p, particle2, location.clone().add(0, height1, 0), x, height1, z, speed2);
                    }
                }
            }
        };

        if (async) {
            FunItems.getInstance().getAsyncExecutor().runAsync(task);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    task.run();
                }
            }.runTask(FunItems.getInstance());
        }
    }
}
