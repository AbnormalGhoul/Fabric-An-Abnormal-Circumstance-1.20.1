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

public class WidowsGemItem extends Item implements Trinket {

    private static final UUID DAMAGE_BONUS_ID = UUID.fromString("f84c8b72-3e6e-4a93-91d5-cb4e8a6eaf01");
    private static final EntityAttributeModifier DAMAGE_BONUS =
            new EntityAttributeModifier(DAMAGE_BONUS_ID, "Widow's Gem bonus", 3.0, EntityAttributeModifier.Operation.ADDITION);

    public WidowsGemItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Trinket.super.onEquip(stack, slot, entity);
        applyBonus(entity);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Trinket.super.onUnequip(stack, slot, entity);
        removeBonus(entity);
    }

    private void applyBonus(LivingEntity entity) {
        EntityAttributeInstance attribute = entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attribute != null && attribute.getModifier(DAMAGE_BONUS_ID) == null) {
            attribute.addPersistentModifier(DAMAGE_BONUS);
        }
    }

    private void removeBonus(LivingEntity entity) {
        EntityAttributeInstance attribute = entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attribute != null && attribute.getModifier(DAMAGE_BONUS_ID) != null) {
            attribute.removeModifier(DAMAGE_BONUS_ID);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, net.minecraft.client.item.TooltipContext context) {
        tooltip.add(Text.literal("When equipped: +3 Attack Damage").formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
