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

public class SeismicCrownSpell extends Spell {
    public SeismicCrownSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_3, 60, 60, icon, "Seismic Crown");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60 * 20, 0, false, false, true));
        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 3.0f, 1.0f);
        // Visual or orbiting entities can be implemented in a tickable component
        return true;
    }
}
