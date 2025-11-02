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

// Water Veil: grants fire resistance for 30s.
public class WaterVeilSpell extends Spell {
    public WaterVeilSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_1, 15, 30, icon, "Water Veil");
    }

    // give fire resistance (amplifier 0) for 30s
    @Override
    public boolean cast(ServerPlayerEntity caster) {
        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.PLAYERS, 3.0f, 1.0f);
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 30 * 20, 0, false, false, true));
        return true;
    }
}