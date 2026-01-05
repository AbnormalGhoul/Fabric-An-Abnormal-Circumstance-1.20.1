package net.abnormal.anabnormalcircumstance.enchantment.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class ManaRegenEnchantment extends Enchantment {

    public ManaRegenEnchantment() {
        super(
                Rarity.RARE,
                EnchantmentTarget.WEARABLE,
                new EquipmentSlot[]{
                        EquipmentSlot.OFFHAND,
                }
        );
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }
}
