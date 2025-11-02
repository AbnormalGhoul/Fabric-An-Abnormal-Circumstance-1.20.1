package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class BloodPactSpell extends Spell {
    public BloodPactSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_3, 65, 150, icon, "Blood Pact");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        caster.damage(world.getDamageSources().magic(), 6.0f); // burns 3 hearts

        List<LivingEntity> allies = world.getEntitiesByClass(LivingEntity.class, caster.getBoundingBox().expand(10), e -> true);
        for (LivingEntity ally : allies) {
            if (ally != caster) {
                // Apply 15% damage increase
            }
        }

        caster.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(StatusEffects.STRENGTH, 15 * 20, 0, false, false, true));
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.PLAYERS, 3.0f, 1.0f);
        return true;
    }
}
