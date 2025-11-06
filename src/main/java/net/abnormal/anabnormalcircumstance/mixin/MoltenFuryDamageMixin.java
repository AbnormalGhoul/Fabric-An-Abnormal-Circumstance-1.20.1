package net.abnormal.anabnormalcircumstance.mixin;

import net.abnormal.anabnormalcircumstance.magic.spells.pyromancy.MoltenFurySpell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class MoltenFuryDamageMixin {

    /**
     * Intercepts the final computed damage AFTER vanilla crits,
     * strength, enchantments, attributes, etc.
     *
     * At RETURN means we catch the value right before it is applied.
     */
    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            argsOnly = true
    )
    private float applyMoltenFuryDamageBoost(float amount, DamageSource source) {

        // Must be player-caused damage
        if (!(source.getAttacker() instanceof ServerPlayerEntity attacker))
            return amount;

        ServerPlayerEntity player = attacker;

        // Only modify damage if Molten Fury is active
        if (!MoltenFurySpell.hasBuff(player))
            return amount;

        // ✅ Apply 1.5× damage multiplier
        float boosted = amount * 1.5f;

        return boosted;
    }
}
