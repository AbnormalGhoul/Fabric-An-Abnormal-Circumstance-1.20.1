package net.abnormal.anabnormalcircumstance.enchantment.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.abnormal.anabnormalcircumstance.enchantment.ModEnchantments;

public final class ManaEnchantments {

    public static int getManaRegenLevel(ServerPlayerEntity player) {
        int total = 0;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            total += EnchantmentHelper.getLevel(
                    ModEnchantments.MANA_REGEN,
                    player.getEquippedStack(slot)
            );
        }

        return Math.min(total, 5);
    }

    public static double getManaRegenMultiplier(ServerPlayerEntity player) {
        return 1.0 + (getManaRegenLevel(player) * 0.10);
    }
}

