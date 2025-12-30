package dev.yooproject;

import dev.yooproject.nms.NMS;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_17_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_17_1 implements NMS {

    @Override
    public void sendParticle(Player player, Particle particle, Location loc, float offsetX, float offsetY, float offsetZ, float speed) {
        ParticleParam param = CraftParticle.toNMS(particle);

        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(param, true, loc.getX(), loc.getY(), loc.getZ(), offsetX, offsetY, offsetZ, speed, 0);

        ((CraftPlayer) player).getHandle().b.sendPacket(packet);
    }
}
