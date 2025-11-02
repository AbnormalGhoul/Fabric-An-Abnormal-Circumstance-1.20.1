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

public class LightAsAFeatherSpell extends Spell {
    public LightAsAFeatherSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_1, 5, 20, icon, "Light as a Feather");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 15 * 20, 0, false, false, true));
        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.PLAYERS, 1.2f, 1.0f);
        return true;
    }
}
