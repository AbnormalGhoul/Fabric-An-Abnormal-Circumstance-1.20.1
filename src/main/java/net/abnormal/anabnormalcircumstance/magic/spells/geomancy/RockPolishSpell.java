package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Blocks;

import java.util.List;

public class RockPolishSpell extends Spell {
    private static final int RADIUS = 10;

    public RockPolishSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_2, 40, 60, icon, "Rock Polish", "Grants nearby allies temporary resistance by coating them in hardened stone.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Sound: deep rumble + stone crack
        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_STONE_PLACE, SoundCategory.PLAYERS, 2.0f, 0.8f);
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.PLAYERS, 1.5f, 1.1f);

        // 🌫Particle: stone dust and ash swirling around caster
        spawnAuraParticles(world, caster);

        // Grant Resistance II (amplifier 1) for 15 seconds to allies in radius
        applyResistanceToAllies(world, caster);

        return true;
    }

    private void spawnAuraParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 1.0, 0);

        for (int i = 0; i < 80; i++) {
            double angle = world.random.nextDouble() * 2 * Math.PI;
            double radius = 2.0 + world.random.nextDouble() * 1.5;
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double y = pos.y + world.random.nextDouble() * 0.5;

            world.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()),
                    x, y, z,
                    1, 0.0, 0.0, 0.0, 0.0
            );

            world.spawnParticles(ParticleTypes.ASH, x, y + 0.2, z, 1, 0, 0, 0, 0);
        }
    }

    private void applyResistanceToAllies(ServerWorld world, ServerPlayerEntity caster) {
        Box area = new Box(
                caster.getX() - RADIUS, caster.getY() - RADIUS, caster.getZ() - RADIUS,
                caster.getX() + RADIUS, caster.getY() + RADIUS, caster.getZ() + RADIUS
        );

        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, area,
                e -> e.isAlive() && (e == caster || caster.isTeammate(e)));

        for (LivingEntity ally : entities) {
            ally.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 25 * 20, 1, false, true, true));

            // Mini particle burst at each ally
            world.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()),
                    ally.getX(), ally.getY() + 1.0, ally.getZ(),
                    20, 0.5, 0.5, 0.5, 0.1
            );
        }
    }
}
