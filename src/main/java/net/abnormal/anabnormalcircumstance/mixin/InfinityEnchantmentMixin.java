package net.abnormal.anabnormalcircumstance.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.InfinityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(InfinityEnchantment.class)
public abstract class InfinityEnchantmentMixin {
    /**
     * Allows Infinity to be compatible with all enchantments except itself.
     * @author
     * @reason
     */
    @Overwrite
    public boolean canAccept(Enchantment other) {
        return !(other instanceof InfinityEnchantment);
    }
}
