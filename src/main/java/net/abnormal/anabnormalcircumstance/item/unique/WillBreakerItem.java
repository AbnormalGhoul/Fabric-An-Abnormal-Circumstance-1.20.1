package net.abnormal.anabnormalcircumstance.item.unique;

import dev.emi.trinkets.api.TrinketsApi;
import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WillBreakerItem extends SwordItem implements UniqueAbilityItem {

    public WillBreakerItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

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

        // Combat logic
        Box area = player.getBoundingBox().expand(5.0);
        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                area,
                e -> e != player && e.isAlive() && !player.isTeammate(e)
        );

        for (LivingEntity target : targets) {
            target.damage(world.getDamageSources().playerAttack(player), 40.0F);
            target.setOnFireFor(5);
            target.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 60, 2));
        }

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 5.0F, 1.0F);
        player.sendMessage(Text.literal("§6Inferno Strike unleashed!").formatted(Formatting.GOLD), true);
        UniqueItemCooldownManager.setCooldown(player, 45 * 1000);

        // Fire swirl particle effect (visible to all)
        spawnFireSwirl((ServerWorld) world, player);
    }

    private void spawnFireSwirl(ServerWorld world, PlayerEntity player) {
        double radius = 2.5;
        double centerY = player.getY() + 1.0;

        for (int i = 0; i < 120; i++) {
            double angle = (2 * Math.PI / 24) * (i % 24);
            double height = (i / 24.0) * 2.0; // swirl up
            double px = player.getX() + Math.cos(angle) * radius;
            double py = centerY + height;
            double pz = player.getZ() + Math.sin(angle) * radius;

            // Fire particle trail
            world.spawnParticles(ParticleTypes.FLAME, px, py, pz, 1, 0, 0, 0, 0);

            // Occasional smoke for contrast
            if (world.random.nextFloat() < 0.2f) {
                world.spawnParticles(ParticleTypes.SMOKE, px, py + 0.1, pz, 1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient()) {
            target.setOnFireFor(10);
            target.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 60, 2));
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient()) return;
        if (entity instanceof PlayerEntity player) {
            boolean holding = selected || player.getOffHandStack() == stack;
            if (holding) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 45, 0, true, false, true));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Causes Bleeding & Fire damage; Grants Fire Resistance").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Inferno Strike - unleash a fiery blast dealing 40 damage").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 45s").formatted(Formatting.GRAY));
    }
}
