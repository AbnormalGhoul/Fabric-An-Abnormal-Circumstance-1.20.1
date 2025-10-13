package net.abnormal.anabnormalcircumstance.event;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.abnormal.anabnormalcircumstance.item.ModItems;

import java.util.UUID;

public class ModAttackEvent {
    // Karambit modifier
    private static final UUID KARAMBIT_UUID = UUID.fromString("b43d1224-1f2a-4a2b-8de8-3e6a5a7a44c2");
    private static final EntityAttributeModifier KARAMBIT_MODIFIER =
            new EntityAttributeModifier(
                    KARAMBIT_UUID,
                    "Karambit Offhand Bonus",
                    3.0,
                    EntityAttributeModifier.Operation.ADDITION
            );

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient() && hand == Hand.MAIN_HAND) {
                ItemStack offhandStack = player.getOffHandStack();

                if (offhandStack.isOf(ModItems.KARAMBIT)) {
                    handleDamageBonus(player, world);
                }
            }
            return ActionResult.PASS;
        });
    }

    private static void handleDamageBonus(PlayerEntity player, World world) {
        var instance = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (instance != null) {
            // Remove existing modifier (by UUID)
            instance.removeModifier(KARAMBIT_UUID);
            instance.addTemporaryModifier(KARAMBIT_MODIFIER);

            // Schedule its removal right after this tick
            world.getServer().execute(() -> instance.removeModifier(KARAMBIT_UUID));
        }
    }
}
