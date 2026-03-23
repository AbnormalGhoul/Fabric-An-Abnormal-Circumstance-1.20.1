package net.abnormal.anabnormalcircumstance.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class JuggernautGemItem extends Item implements Trinket {
    public JuggernautGemItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(
            ItemStack stack,
            SlotReference slot,
            LivingEntity entity,
            UUID uuid
    ) {
        return ImmutableMultimap.of(
                EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
                new EntityAttributeModifier(
                        uuid,
                        "Juggernaut Gem toughness bonus",
                        4.0,
                        EntityAttributeModifier.Operation.ADDITION
                ),
                EntityAttributes.GENERIC_ARMOR,
                new EntityAttributeModifier(
                        UUID.nameUUIDFromBytes((uuid.toString() + ":armor").getBytes()),
                        "Juggernaut Gem armor bonus",
                        4.0,
                        EntityAttributeModifier.Operation.ADDITION
                ),
                EntityAttributes.GENERIC_MAX_HEALTH,
                new EntityAttributeModifier(
                        uuid,
                        "Juggernaut Gem health bonus",
                        20.0,
                        EntityAttributeModifier.Operation.ADDITION
                ),
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(
                        uuid,
                        "Juggernaut Gem attack bonus",
                        6.0,
                        EntityAttributeModifier.Operation.ADDITION
                )
        );
    }
}