package net.abnormal.anabnormalcircumstance.effect.custom;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.util.UUID;

public class CobaltPowerEffect extends StatusEffect {

    private static final UUID SPEED_UUID = UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3");

    public CobaltPowerEffect(StatusEffectCategory category, int color) {
        super(category, color);

        this.addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                SPEED_UUID.toString(),
                0.20,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
        );
        this.addAttributeModifier(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                ATTACK_SPEED_UUID.toString(),
                0.20,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }
}
