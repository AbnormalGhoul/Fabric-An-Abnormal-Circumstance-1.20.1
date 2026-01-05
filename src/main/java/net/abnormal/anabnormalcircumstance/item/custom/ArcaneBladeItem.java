package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class ArcaneBladeItem extends SwordItem {

    public ArcaneBladeItem(
            ToolMaterial material,
            int attackDamage,
            float attackSpeed,
            Settings settings
    ) {
        super(material, attackDamage, attackSpeed, settings);
    }
}
