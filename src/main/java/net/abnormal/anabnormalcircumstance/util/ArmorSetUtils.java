package net.abnormal.anabnormalcircumstance.util;

import net.abnormal.anabnormalcircumstance.item.util.ModArmorMaterials;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public final class ArmorSetUtils {

    private ArmorSetUtils() {} // utility class

    public static boolean hasFullAdamantite(PlayerEntity player) {
        for (ItemStack stack : player.getInventory().armor) {
            if (!(stack.getItem() instanceof ArmorItem armor)) {
                return false;
            }
            if (armor.getMaterial() != ModArmorMaterials.ADAMANTITE) {
                return false;
            }
        }
        return true;
    }
}
