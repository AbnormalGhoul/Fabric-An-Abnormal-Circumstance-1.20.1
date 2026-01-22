package net.abnormal.anabnormalcircumstance.event.custom;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

public class PlayerHeadDropHandler {

    private static final float DROP_CHANCE = 0.05f; // 5%

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(PlayerHeadDropHandler::onDeath);
    }

    private static void onDeath(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof PlayerEntity player)) return;
        if (!(player.getWorld() instanceof ServerWorld world)) return;

        Random random = world.getRandom();
        if (random.nextFloat() > DROP_CHANCE) return;

        ItemStack head = new ItemStack(Items.PLAYER_HEAD);

        // Correct SkullOwner format
        NbtCompound skullOwner = new NbtCompound();
        skullOwner.putUuid("Id", player.getUuid());
        skullOwner.putString("Name", player.getGameProfile().getName());

        head.getOrCreateSubNbt("SkullOwner").copyFrom(skullOwner);

        player.dropStack(head);
    }
}
