package net.abnormal.anabnormalcircumstance.effect.custom;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;

import java.util.UUID;

public class StunEffect extends StatusEffect {
    public StunEffect(StatusEffectCategory category, int color) {
        super(category, color);

        this.addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                STUN_SLOW_UUID.toString(),
                -0.99,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    private static final UUID STUN_SLOW_UUID =
            UUID.fromString("c1c6b2e1-9e5b-4f7a-9c8f-1cde3f7eaaaa");

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

        // Stops all movement every tick
        if (!entity.getWorld().isClient) {
            entity.setVelocity(0.0, entity.getVelocity().y <= 0 ? -0.08 : 0.0, 0.0);
            entity.velocityDirty = true;
        }

        // Prevent jump lift
        entity.fallDistance = 0.0F;

        // Stop mobs
        if (!entity.getWorld().isClient && entity instanceof MobEntity mob) {
            mob.getNavigation().stop();
            mob.getMoveControl().moveTo(mob.getX(), mob.getY(), mob.getZ(), 0);
            mob.getLookControl().lookAt(mob.getX(), mob.getEyeY(), mob.getZ());
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);

        if (!entity.getWorld().isClient) {
            entity.addStatusEffect(
                    new StatusEffectInstance(
                            ModEffects.STUN_IMMUNITY,
                            20 * 3,
                            0,
                            true,
                            true
                    )
            );
        }
    }


    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true; // run every tick
    }
}
