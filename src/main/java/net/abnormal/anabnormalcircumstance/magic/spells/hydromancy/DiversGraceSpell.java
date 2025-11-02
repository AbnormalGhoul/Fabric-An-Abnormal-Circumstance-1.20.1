package net.abnormal.anabnormalcircumstance.magic.spells.hydromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

/**
 * Diver’s Grace:
 * Grants Dolphin’s Grace + Water Breathing for 15 seconds.
 */
public class DiversGraceSpell extends Spell {
    public DiversGraceSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_1, 15, 45, icon, "Diver's Grace");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_DOLPHIN_AMBIENT, SoundCategory.PLAYERS, 3.0f, 1.0f);
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 15 * 20, 0, false, false, true));
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 15 * 20, 0, false, false, true));
        return true;
    }
}
