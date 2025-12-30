package dev.yooproject.funitems.item.animation;

import dev.yooproject.funitems.services.IParticleService;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ItemParticle {
    void start(IParticleService service, Player player, Location startLocation, boolean async);
//    void stop();
//    boolean isRunning();
}
