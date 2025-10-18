package net.abnormal.anabnormalcircumstance.event;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.abnormal.anabnormalcircumstance.item.custom.FirstLeafBowItem;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class ModEvents {
    public static final Identifier UNIQUE_BLADE_ACTIVATE = new Identifier(AnAbnormalCircumstance.MOD_ID, "unique_blade_activate");

    public static void registerEvents() {
        ModAttackEvent.register();

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof LivingEntity target) {
                ItemStack stack = player.getMainHandStack();
                if (stack.getItem() instanceof FirstLeafBowItem && stack.getOrCreateNbt().getBoolean("FirstLeafPrimedArrow")) {
                    FirstLeafBowItem.applyStun(target);
                    stack.getOrCreateNbt().putBoolean("FirstLeafPrimedArrow", false);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

        // Register server-side packet to activate the unique blade ability
        ServerPlayNetworking.registerGlobalReceiver(UNIQUE_BLADE_ACTIVATE, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ItemStack stack = player.getMainHandStack();
                if (stack.getItem() instanceof FirstLeafBowItem) {
                    ((FirstLeafBowItem) stack.getItem()).tryActivateAbility((PlayerEntity) player, stack);
                }
            });
        });
    }
}