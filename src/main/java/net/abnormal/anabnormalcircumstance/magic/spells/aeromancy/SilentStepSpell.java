package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class SilentStepSpell extends Spell {
    public SilentStepSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_2, 45, 180, icon, "Silent Step");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 45 * 20, 0, false, false, true));
        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.PLAYERS, 0.5f, 1.2f);
        return true;
    }
}
