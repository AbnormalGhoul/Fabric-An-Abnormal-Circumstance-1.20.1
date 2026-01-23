package net.abnormal.anabnormalcircumstance.item.unique;

import dev.emi.trinkets.api.TrinketsApi;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;

import java.util.*;

public class LastLeafItem extends SwordItem implements UniqueAbilityItem {
    public LastLeafItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    private static final Map<UUID, DashData> ACTIVE_DASHES = new HashMap<>();

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        World world = player.getWorld();
        if (world.isClient()) return;

        boolean hasMark = TrinketsApi.getTrinketComponent(player)
                .map(comp -> comp.isEquipped(ModItems.CHAMPIONS_CREST))
                .orElse(false);

        if (!hasMark) {
            player.sendMessage(Text.literal("You must equip the Champion's Crest to use this weapon").formatted(Formatting.DARK_RED), true);
            return;
        }

        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)"), true);
            return;
        }

        // Ability logic
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        ServerWorld serverWorld = serverPlayer.getServerWorld();

        // Grapple Logic
        double maxDistance = 32.0;

        Vec3d eyePos = serverPlayer.getEyePos();
        Vec3d lookVec = serverPlayer.getRotationVector();
        Vec3d end = eyePos.add(lookVec.multiply(maxDistance));

        BlockHitResult hit = world.raycast(new net.minecraft.world.RaycastContext(
                eyePos,
                end,
                net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
                net.minecraft.world.RaycastContext.FluidHandling.NONE,
                serverPlayer
        ));

        if (hit.getType() != HitResult.Type.BLOCK) {
            serverPlayer.sendMessage(
                    Text.literal("No surface to latch onto").formatted(Formatting.DARK_GREEN),
                    true
            );
            return;
        }

        // Anchor point slightly off the block face
        Vec3d anchor = hit.getPos().add(Vec3d.of(hit.getSide().getVector()).multiply(0.3));

        // Initial pull direction
        Vec3d pullDir = anchor.subtract(serverPlayer.getPos()).normalize().multiply(1.2);

        ACTIVE_DASHES.put(
                serverPlayer.getUuid(),
                new DashData(12, pullDir, anchor, new HashSet<>())
        );

        // Cooldown, sound, and message
        world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_TRIDENT_RIPTIDE_1, SoundCategory.PLAYERS, 1.5F, 1.2F);
        player.sendMessage(Text.literal("§6Vine Dash!").formatted(Formatting.GOLD), true);
        UniqueItemCooldownManager.setCooldown(player, 45 * 1000);
    }
    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, DashData>> it = ACTIVE_DASHES.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<UUID, DashData> entry = it.next();
                UUID playerId = entry.getKey();
                DashData data = entry.getValue();

                ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
                if (player == null || !player.isAlive()) {
                    it.remove();
                    continue;
                }

                ServerWorld world = player.getServerWorld();

                // Apply velocity forward
                Vec3d toAnchor = data.anchor.subtract(player.getPos());
                double distance = toAnchor.length();

                // Stop early if close enough
                if (distance < 2.0) {
                    it.remove();
                    continue;
                }

                // Smooth pull
                Vec3d pull = toAnchor.normalize().multiply(1.1);
                player.setVelocity(pull);
                player.velocityModified = true;

                // Particles for visual feedback
                spawnLeafParticles(world, player, data.velocity);

                // stun + poison
                affectEntitiesInPath(world, player, data);

                // Countdown
                data.remainingTicks--;
                if (data.remainingTicks <= 0) {
                    it.remove();
                }
            }
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient()) return;
        if (!(entity instanceof PlayerEntity player)) return;

        boolean holding = selected || player.getOffHandStack() == stack;
        if (!holding) return;

        ServerWorld serverWorld = (ServerWorld) world;
        net.abnormal.anabnormalcircumstance.util.FirstLeafBondUtil.handleBondedRegen(serverWorld, player, true);
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Regen I while Held, Regen 3 if blade and bow are held nearby").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Vine Grip - An omnidirectional dash that poisons enemies in the way").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 45s").formatted(Formatting.GRAY));
    }

    private static void affectEntitiesInPath(ServerWorld world, ServerPlayerEntity player, DashData data) {
        Box hitbox = player.getBoundingBox().expand(0.6);
        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                hitbox,
                e -> e != player && e.isAlive() && !player.isTeammate(e)
        );

        for (LivingEntity target : targets) {
            if (data.hit.contains(target.getUuid())) continue;
            data.hit.add(target.getUuid());

            // Damage
            target.damage(world.getDamageSources().magic(), 5.0F);

            // Apply poison
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 1));

            // Knocks player upwards
            Vec3d knockUp = new Vec3d(0, 0.6, 0); // ~2–3 blocks total over gravity
            target.addVelocity(knockUp.x, knockUp.y, knockUp.z);
            target.velocityModified = true;
        }
    }

    private static void spawnLeafParticles(ServerWorld world, ServerPlayerEntity player, Vec3d dir) {
        Vec3d pos = player.getPos().add(0, 1, 0);
        Vec3d back = pos.subtract(dir.normalize().multiply(0.5));

        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, back.x, back.y, back.z, 6, 0.1, 0.1, 0.1, 0.05);
        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, back.x, back.y, back.z, 6, 0.1, 0.1, 0.1, 0.05);
    }

    private static class DashData {
        int remainingTicks;
        Vec3d velocity;
        final Vec3d anchor;
        final Set<UUID> hit;

        DashData(int ticks, Vec3d velocity, Vec3d anchor, Set<UUID> hit) {
            this.remainingTicks = ticks;
            this.velocity = velocity;
            this.anchor = anchor;
            this.hit = hit;
        }
    }

}
