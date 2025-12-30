package dev.yooproject.funitems.item.animation.impl.item;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.item.animation.ItemParticle;
import dev.yooproject.funitems.services.IParticleService;
import dev.yooproject.nms.NMS;
import dev.yooproject.nms.NMSProvider;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class DisorientationParticle implements ItemParticle {

    @Override
    public void start(IParticleService service, Player player, Location location, boolean async) {

        var config = FunItems.getInstance()
                .getConfigurationService()
                .getConfig(dev.yooproject.funitems.configuration.impl.ItemsConfiguration.class)
                .getConfig()
                .getConfigurationSection("items.disorientation.particles");

        if (config == null) return;

        final double radius = config.getDouble("radius");
        final int count = config.getInt("count");
        final float speed = (float) (config.getDouble("velocity") / 1000.0);
        final Particle particle = Particle.valueOf(config.getString("type"));

        final NMS nms = NMSProvider.get();

        Runnable task = () -> {
            for (int i = 0; i < count; i++) {
                double angle = 2 * Math.PI * i / count;
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));

                for (Player p : location.getWorld().getPlayers()) {
                    nms.sendParticle(p, particle, location, x, 0.0f, z, speed);
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
