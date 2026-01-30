package net.abnormal.anabnormalcircumstance.item.unique;

import dev.emi.trinkets.api.TrinketsApi;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.FirstLeafBondUtil;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.fabric_extras.ranged_weapon.api.CustomBow;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FirstLeafBowItem extends CustomBow implements UniqueAbilityItem {
    private static final String PRIMED_ARROW_KEY = "FirstLeafPrimedArrow";
    private static final Map<UUID, MistArea> ACTIVE_MIST = new HashMap<>();

    @SuppressWarnings("deprecation")
    public FirstLeafBowItem(Settings settings) {
        super(settings, () -> Ingredient.EMPTY);
        this.config(new RangedConfig(15, 10.0F, 3.0F));
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;

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

        ItemStack bowStack = player.getMainHandStack().isOf(this)
                ? player.getMainHandStack()
                : player.getOffHandStack().isOf(this)
                ? player.getOffHandStack()
                : ItemStack.EMPTY;

        if (bowStack.isEmpty()) return;

        // Prime next arrow
        bowStack.getOrCreateNbt().putBoolean(PRIMED_ARROW_KEY, true);

        // Warden sound for prime
        player.getWorld().playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_WARDEN_DEATH,
                SoundCategory.PLAYERS,
                5.0f,
                1.0f
        );

        player.sendMessage(Text.literal("Next arrow will summon a mist cloud!").formatted(Formatting.GOLD), true);
        UniqueItemCooldownManager.setCooldown(player, 75 * 1000);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        boolean primed = stack.getOrCreateNbt().getBoolean(PRIMED_ARROW_KEY);

        if (primed && user instanceof PlayerEntity player) {
            stack.getOrCreateNbt().putBoolean(PRIMED_ARROW_KEY, false);
            player.getItemCooldownManager().set(this, 1);
        }

        super.onStoppedUsing(stack, world, user, remainingUseTicks);

        if (!world.isClient && primed && user instanceof PlayerEntity player) {
            PersistentProjectileEntity arrow = world.getEntitiesByClass(
                    PersistentProjectileEntity.class,
                    player.getBoundingBox().expand(64),
                    e -> e.getOwner() == player && !e.isRemoved()
            ).stream().max(Comparator.comparingInt(a -> a.age)).orElse(null);

            if (arrow != null) {
                arrow.setCustomName(Text.literal(PRIMED_ARROW_KEY));
                arrow.setNoGravity(false);
                arrow.setDamage(arrow.getDamage() * 0.9);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient()) return;
        if (!(entity instanceof PlayerEntity player)) return;

        boolean holding = selected || player.getOffHandStack() == stack;
        if (!holding) return;

        ServerWorld serverWorld = (ServerWorld) world;
        FirstLeafBondUtil.handleBondedRegen(serverWorld, player, false);

        if (!world.isClient() && entity.age % 5 == 0) {
            ACTIVE_MIST.values().removeIf(MistArea::isExpired);
        }
    }

    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<MistArea> it = ACTIVE_MIST.values().iterator();
            while (it.hasNext()) {
                MistArea area = it.next();
                if (area.isExpired()) {
                    it.remove();
                    continue;
                }

                ServerWorld world = area.world;
                spawnMistParticles(world, area);
                applyBlindness(world, area);
                area.ticksRemaining--;
            }
        });
    }

    public static void triggerMist(ServerWorld world, Vec3d pos, PlayerEntity caster) {
        ACTIVE_MIST.put(UUID.randomUUID(), new MistArea(world, pos, 7, 15 * 20, caster));
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.AMBIENT, 3.0f, 0.8f);
    }

    private static void spawnMistParticles(ServerWorld world, MistArea area) {
        // Fade density as the mist dissipates
        double lifeRatio = (double) area.ticksRemaining / (15 * 20);
        int particleCount = (int) (40 * lifeRatio); // fewer particles as it fades

        for (int i = 0; i < particleCount; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double distance = world.random.nextDouble() * area.radius;
            double height = (world.random.nextDouble() - 0.5) * area.radius;
            double x = area.center.x + Math.cos(angle) * distance;
            double y = area.center.y + height;
            double z = area.center.z + Math.sin(angle) * distance;

            // Campfire smoke (dense, rising mist)
            world.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, 1, 0.02, 0.1, 0.02, 0.01);
        }
    }

    private static void applyBlindness(ServerWorld world, MistArea area) {
        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                new net.minecraft.util.math.Box(area.center.subtract(area.radius, area.radius, area.radius),
                        area.center.add(area.radius, area.radius, area.radius)),
                e -> {
                    if (!e.isAlive()) return false;
                    if (e instanceof PlayerEntity player) {
                        // Exclude caster and caster's teammates
                        if (player.equals(area.caster) || (area.caster != null && player.isTeammate(area.caster))) {
                            return false;
                        }
                    }
                    return e.getPos().distanceTo(area.center) <= area.radius;
                }
        );

        for (LivingEntity target : targets) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 40, 0, true, true, true));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 40, 1, true, true, true));
        }
    }

    private static class MistArea {
        final ServerWorld world;
        final Vec3d center;
        final double radius;
        final PlayerEntity caster;
        int ticksRemaining;

        MistArea(ServerWorld world, Vec3d center, double radius, int durationTicks, PlayerEntity caster) {
            this.world = world;
            this.center = center;
            this.radius = radius;
            this.ticksRemaining = durationTicks;
            this.caster = caster;
        }

        boolean isExpired() {
            return ticksRemaining <= 0;
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Regen I while Held, Regen 3 if blade and bow are held nearby").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Next fired arrow creates a Mist Cloud for 15s, blinding enemies").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 75s").formatted(Formatting.GRAY));
    }
}
