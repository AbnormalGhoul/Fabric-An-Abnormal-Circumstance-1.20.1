package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

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
import net.minecraft.util.math.Box;

import java.util.List;

public class UpdraftsSpell extends Spell {
    public UpdraftsSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_2, 15, 60, icon, "Updrafts");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        List<LivingEntity> nearby = caster.getWorld().getEntitiesByClass(
                LivingEntity.class,
                new Box(caster.getBlockPos()).expand(10),
                e -> e != caster && e.isAlive()
        );

        for (LivingEntity entity : nearby) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 5 * 20, 0, false, false, true));
        }

        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_PHANTOM_HURT, SoundCategory.PLAYERS, 2.0f, 1.2f);
        return true;
    }
}
