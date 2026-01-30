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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class BroodGemItem extends Item implements Trinket {

    public BroodGemItem(Settings settings) {
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
                        "Brood Gem toughness bonus",
                        2.0,
                        EntityAttributeModifier.Operation.ADDITION
                ),
                EntityAttributes.GENERIC_ARMOR,
                new EntityAttributeModifier(
                        UUID.nameUUIDFromBytes((uuid.toString() + ":armor").getBytes()),
                        "Brood Gem armor bonus",
                        2.0,
                        EntityAttributeModifier.Operation.ADDITION
                )
        );
    }
}
