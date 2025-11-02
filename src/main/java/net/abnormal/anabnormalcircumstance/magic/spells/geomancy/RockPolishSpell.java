package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class RockPolishSpell extends Spell {
    public RockPolishSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_2, 35, 75, icon, "Rock Polish");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        List<LivingEntity> allies = world.getEntitiesByClass(LivingEntity.class, caster.getBoundingBox().expand(10), e -> true);

        for (LivingEntity ally : allies) {
            ally.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 20, 1, false, false, true));
        }

        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_STONE_PLACE, SoundCategory.PLAYERS, 2.0f, 1.0f);
        return true;
    }
}
