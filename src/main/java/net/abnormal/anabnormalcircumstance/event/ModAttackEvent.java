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

import java.util.Objects;
import java.util.UUID;

public class ModAttackEvent {
    private static final UUID ICICLE_UUID = UUID.fromString("e9a17ef3-4280-4e9b-9a37-f0d4517e6e22");
    private static final EntityAttributeModifier ICICLE_MODIFIER =
            new EntityAttributeModifier(ICICLE_UUID, "Icicle Shard Offhand Bonus", 3.0, EntityAttributeModifier.Operation.ADDITION);

    private static final UUID KARAMBIT_UUID = UUID.fromString("b43d1224-1f2a-4a2b-8de8-3e6a5a7a44c2");
    private static final EntityAttributeModifier KARAMBIT_MODIFIER =
            new EntityAttributeModifier(KARAMBIT_UUID, "Karambit Offhand Bonus", 3.0, EntityAttributeModifier.Operation.ADDITION);

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient() && hand == Hand.MAIN_HAND) {
                ItemStack offhandStack = player.getOffHandStack();

                if (offhandStack.isOf(ModItems.KARAMBIT)) {
                    handleDamageBonus(player, world, KARAMBIT_UUID, KARAMBIT_MODIFIER);
                } else if (offhandStack.isOf(ModItems.ICICLE_SHARD)) {
                    handleDamageBonus(player, world, ICICLE_UUID, ICICLE_MODIFIER);
                }
            }
            return ActionResult.PASS;
        });
    }

    private static void handleDamageBonus(PlayerEntity player, World world, UUID id, EntityAttributeModifier modifier) {
        var instance = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (instance != null) {
            instance.removeModifier(id);
            instance.addTemporaryModifier(modifier);
            Objects.requireNonNull(world.getServer()).execute(() -> instance.removeModifier(id));
        }
    }
}
