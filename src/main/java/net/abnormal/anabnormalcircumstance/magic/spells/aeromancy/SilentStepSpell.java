package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import com.mojang.datafixers.util.Pair;


import java.util.*;

/**
 * Silent Step Spell (Aeromancy Tier 2)
 * Cost: 45 Mana | Cooldown: 3 min | Duration: 60s
 * True invisibility (no armor/items/footsteps)
 */
public class SilentStepSpell extends Spell {

    private static final int DURATION_TICKS = 60 * 20;
    private static final Set<UUID> INVISIBLE_PLAYERS = new HashSet<>();
    private static final Map<UUID, Map<EquipmentSlot, ItemStack>> STORED_EQUIPMENT = new HashMap<>();
    private static boolean ATTACK_LISTENER_REGISTERED = false;

    public SilentStepSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_2, 45, 180, icon, "Silent Step");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.5f, 1.2f);
        world.playSound(null, caster.getBlockPos(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.9f, 1.6f);
        world.spawnParticles(ParticleTypes.CLOUD,
                caster.getX(), caster.getY() + 1.0, caster.getZ(),
                50, 0.6, 1.0, 0.6, 0.02);

        applyTrueInvisibility(caster);
        registerAttackDamageCancel();
        return true;
    }

    private void applyTrueInvisibility(ServerPlayerEntity player) {
        if (INVISIBLE_PLAYERS.contains(player.getUuid())) return;
        INVISIBLE_PLAYERS.add(player.getUuid());

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, DURATION_TICKS, 0, false, false, true));

        // Store equipment to restore later
        Map<EquipmentSlot, ItemStack> equipment = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            equipment.put(slot, player.getEquippedStack(slot).copy());
        }
        STORED_EQUIPMENT.put(player.getUuid(), equipment);

        // Hide from others and clear equipped visuals
        hideFromOthers(player);

        player.sendMessage(Text.literal("You fade into silence..."), true);

        schedule(player.getServer(), DURATION_TICKS, () -> removeInvisibility(player));
    }

    public static void removeInvisibility(ServerPlayerEntity player) {
        if (player == null || player.getServerWorld().isClient) return;
        UUID id = player.getUuid();
        if (!INVISIBLE_PLAYERS.contains(id)) return;
        INVISIBLE_PLAYERS.remove(id);

        ServerWorld world = player.getServerWorld();

        // Respawn visible entity for others
        for (ServerPlayerEntity other : world.getPlayers(p -> p != player)) {
            other.networkHandler.sendPacket(new EntitySpawnS2CPacket(player));
            // Re-sync equipment after respawn
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = player.getEquippedStack(slot);
                other.networkHandler.sendPacket(
                        new EntityEquipmentUpdateS2CPacket(player.getId(), List.of(Pair.of(slot, stack)))
                );
            }
        }

        player.removeStatusEffect(StatusEffects.INVISIBILITY);
        STORED_EQUIPMENT.remove(id);

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, 1.0f, 1.2f);
        world.spawnParticles(ParticleTypes.END_ROD,
                player.getX(), player.getY() + 1.0, player.getZ(),
                40, 0.5, 1.0, 0.5, 0.01);
    }

    private static void hideFromOthers(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        int id = player.getId();

        // Send destroy packet so the model disappears
        for (ServerPlayerEntity other : world.getPlayers(p -> p != player)) {
            other.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(id));
        }

        // Send equipment updates with empty stacks so armor/weapons vanish even in entity refresh
        for (ServerPlayerEntity other : world.getPlayers(p -> p != player)) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                other.networkHandler.sendPacket(
                        new EntityEquipmentUpdateS2CPacket(id, List.of(Pair.of(slot, ItemStack.EMPTY)))
                );

            }
        }

        // Hide footsteps by clearing movement dust packets each tick
        ServerTickEvents.END_WORLD_TICK.register(w -> {
            if (!INVISIBLE_PLAYERS.contains(player.getUuid())) return;
            // Remove sprinting state on client by faking sneaking or silent motion (optional)
            player.setSprinting(false);
        });
    }

    private void registerAttackDamageCancel() {
        if (ATTACK_LISTENER_REGISTERED) return;
        ATTACK_LISTENER_REGISTERED = true;

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && INVISIBLE_PLAYERS.contains(player.getUuid())) {
                removeInvisibility((ServerPlayerEntity) player);
            }
            return ActionResult.PASS;
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (INVISIBLE_PLAYERS.contains(player.getUuid()) && player.hurtTime > 0) {
                    removeInvisibility(player);
                }
            }
        });
    }

    // Simple internal scheduler (like IcicleShatterSpell)
    private static final List<Task> TASKS = new ArrayList<>();

    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Task> it = TASKS.iterator();
            while (it.hasNext()) {
                Task t = it.next();
                if (--t.ticks <= 0) {
                    t.run.run();
                    it.remove();
                }
            }
        });
    }

    private static void schedule(MinecraftServer server, int delay, Runnable run) {
        TASKS.add(new Task(delay, run));
    }

    private static class Task {
        int ticks;
        final Runnable run;
        Task(int ticks, Runnable run) { this.ticks = ticks; this.run = run; }
    }
}
