package dev.yooproject;

import dev.yooproject.nms.NMS;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_16_R3.ParticleParam;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_16_R3.CraftParticle;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_16_5 implements NMS {

    @Override
    public void sendParticle(Player player, Particle particle, Location loc, float offsetX, float offsetY, float offsetZ, float speed) {
        ParticleParam param = CraftParticle.toNMS(particle);

        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(param, true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), offsetX, offsetY, offsetZ, speed, 0);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
