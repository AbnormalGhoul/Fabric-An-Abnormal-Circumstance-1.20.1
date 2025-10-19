package net.abnormal.anabnormalcircumstance.util;


import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UniqueItemCooldownManager {
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final String COOLDOWN_KEY = "UniqueItemCooldown";

    public static boolean isOnCooldown(PlayerEntity player) {
        Long until = cooldowns.get(player.getUuid());
        return until != null && System.currentTimeMillis() < until;
    }

    public static void setCooldown(PlayerEntity player, long millis) {
        cooldowns.put(player.getUuid(), System.currentTimeMillis() + millis);
    }

    public static long getRemaining(PlayerEntity player) {
        Long until = cooldowns.get(player.getUuid());
        if (until == null) return 0;
        return Math.max(0, until - System.currentTimeMillis());
    }
}