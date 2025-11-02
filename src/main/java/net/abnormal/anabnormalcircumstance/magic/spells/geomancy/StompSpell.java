package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class StompSpell extends Spell {
    public StompSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_2, 40, 45, icon, "Stomp");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        caster.addVelocity(0, 1.0, 0);
        caster.velocityModified = true;

        List<Entity> entities = world.getOtherEntities(caster, new Box(caster.getBlockPos()).expand(5));
        for (Entity e : entities) {
            if (e instanceof LivingEntity living && living != caster) {
                living.damage(world.getDamageSources().magic(), 10.0f);
                living.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(StatusEffects.SLOWNESS, 60, 4, false, false, true)); // "stun" mimic
            }
        }

        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 3.0f, 0.7f);
        return true;
    }
}
