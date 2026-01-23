package net.abnormal.anabnormalcircumstance.event.custom;

import net.abnormal.anabnormalcircumstance.util.ArmorSetUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdamantiteBonusHandler {

    private static final Map<PlayerEntity, Integer> hitCounter = new HashMap<>();

    private static final UUID ADAMANTITE_BONUS_ID = UUID.fromString("b1629e14-289a-4ab9-8a34-d94b2b1a9c99");
    private static final EntityAttributeModifier ADAMANTITE_BONUS =
            new EntityAttributeModifier(ADAMANTITE_BONUS_ID, "Adamantite set bonus", 3.0, EntityAttributeModifier.Operation.ADDITION);

    // Called when player attacks an entity.
    public static void onPlayerAttack(PlayerEntity player, Entity target) {
        if (!(target instanceof LivingEntity)) return;
        World world = player.getWorld();
        if (world.isClient) return;
        if (!ArmorSetUtils.hasFullAdamantite(player)) return;

        int count = hitCounter.getOrDefault(player, 0) + 1;

        if (count >= 3) {
            hitCounter.put(player, 0);

            EntityAttributeInstance attackDamage = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (attackDamage != null && attackDamage.getModifier(ADAMANTITE_BONUS_ID) == null) {
                // Apply +3 damage only for this swing
                attackDamage.addTemporaryModifier(ADAMANTITE_BONUS);
            }

            world.playSound(
                    null,
                    target.getBlockPos(),
                    SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
                    SoundCategory.PLAYERS,
                    5f,
                    1.84f
            );

        } else {
            hitCounter.put(player, count);
        }
    }

    // Called each server tick to remove the temp modifier after the hit has been processed.
    public static void tick(PlayerEntity player) {
        var attr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attr != null && attr.getModifier(ADAMANTITE_BONUS_ID) != null) {
            attr.removeModifier(ADAMANTITE_BONUS_ID);
        }
    }

    public static void reset(PlayerEntity player) {
        hitCounter.remove(player);
    }
}
