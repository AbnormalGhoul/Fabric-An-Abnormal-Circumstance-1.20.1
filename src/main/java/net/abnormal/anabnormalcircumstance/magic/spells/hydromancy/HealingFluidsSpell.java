package net.abnormal.anabnormalcircumstance.magic.spells.hydromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class HealingFluidsSpell extends Spell {

    public HealingFluidsSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_3, 40, 60, icon, "Healing Fluids", "Releases a burst of medicinal water that heals you and nearby allies.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Sound and swirl
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED, SoundCategory.PLAYERS, 2.0f, 1.0f);
        spawnWaterSwirl(world, caster, 1.0, 2.0, 80);

        double range = 3.0;
        Box area = new Box(
                caster.getX() - range, caster.getY() - range, caster.getZ() - range,
                caster.getX() + range, caster.getY() + range, caster.getZ() + range
        );

        // Heal allies and caster
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, area,
                entity -> entity instanceof LivingEntity && (caster.isTeammate(entity) || entity == caster));

        for (LivingEntity target : targets) {
            float healAmount = 16.0f; // 8 hearts
            target.heal(healAmount);
        }

        return true;
    }

//     Creates a swirling helix of splash particles around the caster.
    private void spawnWaterSwirl(ServerWorld world, ServerPlayerEntity caster, double radius, double height, int points) {
        Vec3d origin = caster.getPos().add(0, 1, 0);
        for (int i = 0; i < points; i++) {
            double progress = (double) i / points;
            double angle = progress * Math.PI * 5; // about 2.5 full rotations
            double y = origin.y + progress * height;
            double x = origin.x + radius * Math.cos(angle);
            double z = origin.z + radius * Math.sin(angle);

            world.spawnParticles(ParticleTypes.SPLASH, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
