package net.abnormal.anabnormalcircumstance.magic;

import net.abnormal.anabnormalcircumstance.util.ArmorSetUtils;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Applies cooldown modifiers based on equipment.
 */
public final class SpellCooldownUtil {

    private static final int MITHRIL_REDUCTION_TICKS = 10 * 20;

    private SpellCooldownUtil() {}

    public static int applyCooldownModifiers(ServerPlayerEntity player, int baseTicks) {
        int result = baseTicks;

        if (ArmorSetUtils.hasFullMithril(player)) {
            result -= MITHRIL_REDUCTION_TICKS;
        }

        return Math.max(0, result);
    }
}
