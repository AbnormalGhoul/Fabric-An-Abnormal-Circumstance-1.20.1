package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class RingOfFireSpell extends Spell {
    public RingOfFireSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_3, 35, 30, icon, "Ring of Fire");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        for (int wave = 0; wave < 3; wave++) {
            int radius = 3 + wave * 2;
            for (int i = 0; i < 36; i++) {
                double angle = Math.toRadians(i * 10);
                double x = caster.getX() + Math.cos(angle) * radius;
                double z = caster.getZ() + Math.sin(angle) * radius;
                world.addParticle(ParticleTypes.FLAME, x, caster.getY(), z, 0, 0.01, 0);
            }

            List<Entity> entities = world.getOtherEntities(caster, caster.getBoundingBox().expand(radius));
            for (Entity e : entities) {
                if (e instanceof LivingEntity target && target != caster) {
                    target.setOnFireFor(4);
                    target.damage(world.getDamageSources().magic(), 6);
                }
            }
        }

        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS, 3.0f, 1.0f);
        return true;
    }
}
