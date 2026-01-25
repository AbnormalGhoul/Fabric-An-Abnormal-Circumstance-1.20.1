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

public class WidowsGemItem extends Item implements Trinket {

    public WidowsGemItem(Settings settings) {
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
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(
                        uuid,
                        "Widow's Gem attack bonus",
                        3.0,
                        EntityAttributeModifier.Operation.ADDITION
                )
        );
    }
}
