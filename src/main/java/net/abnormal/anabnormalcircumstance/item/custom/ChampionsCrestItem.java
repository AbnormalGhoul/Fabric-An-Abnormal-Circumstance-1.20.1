package net.abnormal.anabnormalcircumstance.item.custom;

import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ChampionsCrestItem extends Item implements Trinket {

    private static final UUID HEALTH_BONUS_ID = UUID.fromString("6c2e1c23-338a-4cfa-8b48-772b3a927a4b");
    private static final EntityAttributeModifier HEALTH_BONUS =
            new EntityAttributeModifier(HEALTH_BONUS_ID, "Mark of a Champion health bonus", 4.0, EntityAttributeModifier.Operation.ADDITION);
    // 4.0 = +2 hearts (each heart = 2 health)

    public ChampionsCrestItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Trinket.super.onEquip(stack, slot, entity);
        applyHealthBonus(entity);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Trinket.super.onUnequip(stack, slot, entity);
        removeHealthBonus(entity);
    }

    private void applyHealthBonus(LivingEntity entity) {
        EntityAttributeInstance attribute = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attribute != null && attribute.getModifier(HEALTH_BONUS_ID) == null) {
            attribute.addPersistentModifier(HEALTH_BONUS);
            // Heal player to new max HP to prevent partial-health issues
            entity.setHealth(entity.getMaxHealth());
        }
    }

    private void removeHealthBonus(LivingEntity entity) {
        EntityAttributeInstance attribute = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attribute != null && attribute.getModifier(HEALTH_BONUS_ID) != null) {
            attribute.removeModifier(HEALTH_BONUS_ID);
            // Ensure health is within valid range after removing modifier
            if (entity.getHealth() > entity.getMaxHealth()) {
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, net.minecraft.client.item.TooltipContext context) {
        tooltip.add(Text.literal("When equipped: +2 Max Hearts").formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
