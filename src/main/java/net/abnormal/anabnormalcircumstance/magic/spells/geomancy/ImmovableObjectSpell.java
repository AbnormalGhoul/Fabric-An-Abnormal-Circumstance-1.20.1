package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ImmovableObjectSpell extends Spell {
    public ImmovableObjectSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_4, 75, 900, icon, "Immovable Object");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 25 * 20, 4, false, false, true));
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 25 * 20, 1, false, false, true));
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 25 * 20, 1, false, false, true));
        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 3.0f, 0.6f);
        return true;
    }
}
