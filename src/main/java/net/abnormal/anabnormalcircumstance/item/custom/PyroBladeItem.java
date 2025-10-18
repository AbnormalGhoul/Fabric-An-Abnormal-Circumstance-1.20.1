package net.abnormal.anabnormalcircumstance.item.custom;

import net.abnormal.anabnormalcircumstance.item.ModToolMaterials;
import net.abnormal.anabnormalcircumstance.network.ModPackets;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PyroBladeItem extends UniqueBladeItem {
    public PyroBladeItem(Settings settings) {
        super(ModToolMaterials.UNIQUE, 8, -2.4F, settings, 60_000);
    }

    @Override
    protected void onHitEffect(PlayerEntity player, LivingEntity target, ItemStack stack) {
        target.setOnFireFor(5);
    }

    @Override
    protected void activateAbility(PlayerEntity player, ItemStack stack) {
        World world = player.getWorld();
        Box area = player.getBoundingBox().expand(3.0);
        for (Entity entity : world.getEntitiesByClass(LivingEntity.class, area, e -> e != player)) {
            entity.damage(world.getDamageSources().magic(), 25.0F);
        }
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ModPackets.sendBladeSound(serverPlayer, SoundEvents.ENTITY_BLAZE_SHOOT.getId().toString());
        }
        // Spawn fire and red particles in a pillar
        if (world.isClient) return;
        for (int y = 0; y < 5; y++) {
            double py = player.getY() + y;
            for (int i = 0; i < 16; i++) {
                double angle = (2 * Math.PI / 16) * i;
                double px = player.getX() + Math.cos(angle) * 1.5;
                double pz = player.getZ() + Math.sin(angle) * 1.5;
                ((ServerWorld)world).spawnParticles(ParticleTypes.FLAME, px, py, pz, 1, 0, 0, 0, 0);
                ((ServerWorld)world).spawnParticles(ParticleTypes.CRIMSON_SPORE, px, py, pz, 1, 0, 0, 0, 1);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Burns enemies on hit").formatted(Formatting.RED));
        tooltip.add(Text.literal("Active: Magma Swing - 25 damage in 3-block radius (R)").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 60s").formatted(Formatting.GRAY));
    }
}