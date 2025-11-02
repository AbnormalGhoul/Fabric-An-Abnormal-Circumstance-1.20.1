package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class MoltenFurrySpell extends Spell {
    public MoltenFurrySpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_4, 100, 900, icon, "Molten Furry");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_LAVA_POP, SoundCategory.PLAYERS, 4.0f, 1.0f);
        // 50% damage increase goes here
        return true;
    }
}
