package net.abnormal.anabnormalcircumstance.enchantment;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.enchantment.custom.ManaRegenEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEnchantments {

    public static Enchantment MANA_REGEN;

    public static void register() {
        MANA_REGEN = Registry.register(
                Registries.ENCHANTMENT,
                new Identifier(AnAbnormalCircumstance.MOD_ID, "mana_regen"),
                new ManaRegenEnchantment()
        );
    }
}
