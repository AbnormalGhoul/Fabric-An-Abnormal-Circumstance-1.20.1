package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class SwiftStepSpell extends Spell {

    // Roughly 8 blocks worth of momentum
    private static final double DASH_STRENGTH = 1.0;

    public SwiftStepSpell(Identifier id, Identifier icon) {
        super(
                id,
                SpellElement.AEROMANCY,
                SpellTier.TIER_1,
                20,
                30,
                icon,
                "Swift Step",
                "A burst of wind propels you swiftly forward."
        );
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Direction player is looking
        Vec3d lookDir = caster.getRotationVec(1.0f).normalize();

        // Add forward velocity, preserve some existing momentum
        Vec3d dashVelocity = lookDir.multiply(DASH_STRENGTH)
                .add(0, 0.1, 0); // slight lift to prevent ground snagging

        caster.addVelocity(dashVelocity);
        caster.velocityModified = true;

        // Dash sound
        world.playSound(
                null,
                caster.getBlockPos(),
                SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                SoundCategory.PLAYERS,
                1.2f,
                1.6f
        );

        spawnDashParticles(world, caster);

        return true;
    }

    private void spawnDashParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 1.0, 0);

        DustParticleEffect wind = new DustParticleEffect(
                new Vector3f(0.7f, 0.85f, 1.0f),
                1.0f
        );

        for (int i = 0; i < 25; i++) {
            double ox = (world.getRandom().nextDouble() - 0.5) * 0.6;
            double oy = world.getRandom().nextDouble() * 0.6;
            double oz = (world.getRandom().nextDouble() - 0.5) * 0.6;

            world.spawnParticles(
                    wind,
                    pos.x + ox,
                    pos.y + oy,
                    pos.z + oz,
                    1,
                    0,
                    0,
                    0,
                    0
            );
        }
    }
}
