package dev.yooproject.funitems.services;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RadiusService {

    private final double maxRadius;

    public RadiusService(double maxRadius) {
        this.maxRadius = maxRadius;
    }

    /**
     * Получить максимальный радиус действия
     */
    public double getMaxRadius() {
        return maxRadius;
    }

    /**
     * Рассчитывает множитель времени эффекта в зависимости от расстояния.
     * 1.0 — вблизи, 0 — на границе радиуса.
     */
    public double getMultiplier(Location source, Player target) {
        double distance = source.distance(target.getLocation());
        if (distance > maxRadius) return 0;
        return 1 - (distance / maxRadius);
    }
}
