package net.abnormal.anabnormalcircumstance.util;

import net.abnormal.anabnormalcircumstance.item.unique.SolinAxeItem;
import net.abnormal.anabnormalcircumstance.item.unique.SolinSwordItem;
import net.minecraft.entity.player.PlayerEntity;

public class UniqueAbilityHelper {
    public static boolean hasBothSolinWeapons(PlayerEntity player) {
        boolean hasSword = player.getMainHandStack().getItem() instanceof SolinSwordItem ||
                player.getOffHandStack().getItem() instanceof SolinSwordItem;
        boolean hasAxe = player.getMainHandStack().getItem() instanceof SolinAxeItem ||
                player.getOffHandStack().getItem() instanceof SolinAxeItem;
        return hasSword && hasAxe;
    }
}
