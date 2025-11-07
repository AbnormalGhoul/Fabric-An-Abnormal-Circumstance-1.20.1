package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class GaleWindsSpell extends Spell {

    private static final double DASH_STRENGTH = 2.8; // Forward push multiplier
    private static final double VERTICAL_BOOST = 0.3; // Slight lift to feel airy
    private static final int PARTICLE_COUNT = 50;

    public GaleWindsSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_3, 20, 30, icon, "Gale Winds");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Launch player forward in their facing direction
        Vec3d look = caster.getRotationVec(1.0F).normalize();
        Vec3d dash = new Vec3d(look.x * DASH_STRENGTH, look.y * 0.5 + VERTICAL_BOOST, look.z * DASH_STRENGTH);

        caster.addVelocity(dash.x, dash.y, dash.z);
        caster.velocityModified = true;

        // Play sound (dragon wing flap)
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1.5f, 1.0f);

        // Spawn particle trail
        spawnWindTrail(world, caster);

        return true;
    }

    private void spawnWindTrail(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * 1.5;
            double height = Math.random() * 1.5;
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double y = pos.y + 0.5 + height * 0.5;

            world.spawnParticles(
                    ParticleTypes.CLOUD,
                    x,
                    y,
                    z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );
        }
    }
}
