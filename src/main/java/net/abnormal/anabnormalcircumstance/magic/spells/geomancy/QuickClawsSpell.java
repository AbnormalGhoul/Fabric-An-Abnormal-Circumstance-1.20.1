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

public class QuickClawsSpell extends Spell {
    public QuickClawsSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_1, 25, 45, icon, "Quick Claws");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 25 * 20, 1, false, false, true));
        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 2.0f, 1.0f);
        return true;
    }
}
