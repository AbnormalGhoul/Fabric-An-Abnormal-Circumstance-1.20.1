package net.abnormal.anabnormalcircumstance.magic.spells.hydromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

/**
 * Cleansing Fluids:
 * Removes negative effects and grants absorption hearts to allies within 10 blocks.
 */
public class CleansingFluidsSpell extends Spell {
    public CleansingFluidsSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_3, 45, 60, icon, "Cleansing Fluids");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED, SoundCategory.PLAYERS, 3.0f, 1.0f);

        Box area = new Box(caster.getBlockPos()).expand(10.0);
        List<Entity> nearby = world.getOtherEntities(caster, area, e -> e instanceof LivingEntity);

        for (Entity e : nearby) {
            LivingEntity target = (LivingEntity) e;
            target.clearStatusEffects();
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 20, 1, false, false, true));
        }

        caster.clearStatusEffects();
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 20, 1, false, false, true));
        return true;
    }
}
