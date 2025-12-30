package dev.yooproject.nms;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public interface NMS {
    void sendParticle(Player player, Particle particle, Location location, float offsetX, float offsetY, float offsetZ, float speed);
}
