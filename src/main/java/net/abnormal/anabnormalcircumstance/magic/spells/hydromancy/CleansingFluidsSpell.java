package net.abnormal.anabnormalcircumstance.magic.spells.hydromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.List;

public class CleansingFluidsSpell extends Spell {

    public CleansingFluidsSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_3, 45, 60, icon, "Cleansing Fluids", "Cleans off all negative effects and grants temporary absorption to you and allies.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Sound and swirl
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED, SoundCategory.PLAYERS, 2.0f, 1.0f);
        spawnWaterSwirl(world, caster, 1.2, 2.5, 100);

        double range = 10.0;
        Box area = new Box(
                caster.getX() - range, caster.getY() - range, caster.getZ() - range,
                caster.getX() + range, caster.getY() + range, caster.getZ() + range
        );

        // Affect caster + nearby allies
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, area,
                entity -> entity instanceof LivingEntity && (caster.isTeammate(entity) || entity == caster));

        for (LivingEntity target : targets) {
            removeNegativeEffects(target);
            removeMovementBuffs(target);
        }


        // Give caster 4 absorption hearts (8 health)
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 60, 1, false, false, true));

        return true;
    }

//     Removes all *negative* status effects from an entity.
//     (Negative = effect.isBeneficial() == false)
    private void removeNegativeEffects(LivingEntity entity) {
        Collection<StatusEffectInstance> effects = entity.getStatusEffects().stream().toList();
        for (StatusEffectInstance effect : effects) {
            StatusEffect type = effect.getEffectType();
            if (!type.isBeneficial()) {
                entity.removeStatusEffect(type);
            }
        }
    }

    /**
     * Creates a swirling ring of water particles around the caster.
     * @param world   The server world.
     * @param caster  The player casting the spell.
     * @param radius  Horizontal radius of the swirl.
     * @param height  Vertical height of the spiral.
     * @param points  How many particles to use.
     */
    private void spawnWaterSwirl(ServerWorld world, ServerPlayerEntity caster, double radius, double height, int points) {
        Vec3d origin = caster.getPos().add(0, 1, 0);
        for (int i = 0; i < points; i++) {
            double progress = (double) i / points;
            double angle = progress * Math.PI * 6; // 3 full rotations
            double y = origin.y + progress * height;
            double x = origin.x + radius * Math.cos(angle);
            double z = origin.z + radius * Math.sin(angle);

            world.spawnParticles(ParticleTypes.SPLASH, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private void removeMovementBuffs(LivingEntity entity) {
        entity.removeStatusEffect(StatusEffects.JUMP_BOOST);
        entity.removeStatusEffect(StatusEffects.SLOW_FALLING);
    }

}
