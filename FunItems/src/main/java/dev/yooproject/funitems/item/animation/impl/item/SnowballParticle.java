package dev.yooproject.funitems.item.animation.impl.item;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.item.animation.ItemParticle;
import dev.yooproject.nms.NMS;
import dev.yooproject.nms.NMSProvider;
import dev.yooproject.funitems.services.IParticleService;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class SnowballParticle implements ItemParticle {

    @Override
    public void start(IParticleService service, Player player, Location location, boolean async) {

        final NMS nms = NMSProvider.get();

        Runnable task = () -> {
            for (int i = 0; i < 80; i++) {
                float offsetX = (float) (Math.random() * 6.0 - 3.0);
                float offsetY = (float) (Math.random() * 6.0 - 3.0);
                float offsetZ = (float) (Math.random() * 6.0 - 3.0);

                for (Player p : location.getWorld().getPlayers()) {
                    nms.sendParticle(p, Particle.CLOUD, location, offsetX, offsetY, offsetZ, 0.1f);
                }
            }

            for (Player p : location.getWorld().getPlayers()) {
                nms.sendParticle(p, Particle.EXPLOSION_HUGE, location, 0f, 0f, 0f, 0f);
            }
        };

        if (async) {
            FunItems.getInstance().getAsyncExecutor().runAsync(task);
        } else {
            task.run();
        }
    }
}