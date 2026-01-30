package net.abnormal.anabnormalcircumstance.magic.spells.hydromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.*;

public class BileWaterSpell extends Spell {
    private static final Map<UUID, Boolean> ACTIVE_EFFECT = new HashMap<>();

    public BileWaterSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_2, 40, 60, icon, "Bile Water", "Coats your next attack with tainted water that sickens the target.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        world.playSound(null, caster.getBlockPos(),
                SoundEvents.BLOCK_FROGSPAWN_HATCH,
                SoundCategory.PLAYERS, 1.5f, 1.0f);

        spawnSwampParticles(world, caster);
        ACTIVE_EFFECT.put(caster.getUuid(), true);

        return true;
    }

    private void spawnSwampParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 1.0, 0);
        var random = world.getRandom();

        DustParticleEffect particle = new DustParticleEffect(new Vector3f(0.3f, 0.8f, 0.3f), 1.0f);
        for (int i = 0; i < 40; i++) {
            double ox = (random.nextDouble() - 0.5) * 1.5;
            double oy = random.nextDouble();
            double oz = (random.nextDouble() - 0.5) * 1.5;

            // use overload that guarantees client visibility
            world.spawnParticles(
                    caster, particle, true,
                    pos.x + ox, pos.y + oy, pos.z + oz,
                    1, 0, 0, 0, 0
            );
        }
    }

    public static void onEntityAttacked(LivingEntity attacker, LivingEntity target, DamageSource source) {
        if (!(attacker instanceof ServerPlayerEntity player)) return;
        UUID uuid = player.getUuid();

        if (ACTIVE_EFFECT.getOrDefault(uuid, false)) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 5 * 20));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 10 * 20, 2));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 5 * 20));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 10 * 20));
            ACTIVE_EFFECT.remove(uuid);
        }
    }

    public static void clearBuff(ServerPlayerEntity player) {
        ACTIVE_EFFECT.remove(player.getUuid());
    }
}
