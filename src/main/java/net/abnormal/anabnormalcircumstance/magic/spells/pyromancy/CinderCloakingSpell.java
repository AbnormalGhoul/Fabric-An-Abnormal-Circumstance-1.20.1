package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class CinderCloakingSpell extends Spell {
    public CinderCloakingSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_1, 15, 120, icon, "Cinder Cloaking");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        List<Entity> nearby = world.getOtherEntities(caster, caster.getBoundingBox().expand(30));

        for (Entity entity : nearby) {
            if (entity instanceof net.minecraft.entity.LivingEntity living && !(entity instanceof ServerPlayerEntity ally)) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20 * 20, 0, false, false, true));
            }
        }

        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 2.0f, 1.2f);
        return true;
    }
}
