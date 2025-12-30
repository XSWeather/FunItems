package dev.yooproject.funitems.services;

import dev.yooproject.funitems.async.AsyncExecutor;
import dev.yooproject.funitems.item.animation.IParticleType;
import dev.yooproject.funitems.item.animation.ItemParticle;
import dev.yooproject.funitems.item.animation.impl.item.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Map;

public class IParticleService {

    private final Map<IParticleType, ItemParticle> animations = new EnumMap<>(IParticleType.class);
    private final AsyncExecutor executor;

    public IParticleService(AsyncExecutor executor) {
        this.executor = executor;
        animations.put(IParticleType.DISORIENTATION, new DisorientationParticle());
        animations.put(IParticleType.DUST, new DustParticle());
        animations.put(IParticleType.SMERCH, new SmerchParticle());
        animations.put(IParticleType.SNOWBALL, new SnowballParticle());
        animations.put(IParticleType.AURA, new AuraParticle());
    }

    public void play(Player player, IParticleType type, Location startLocation) {
        ItemParticle anim = animations.get(type);
        if (anim == null) return;
        anim.start(this, player, startLocation, true);
    }
}
