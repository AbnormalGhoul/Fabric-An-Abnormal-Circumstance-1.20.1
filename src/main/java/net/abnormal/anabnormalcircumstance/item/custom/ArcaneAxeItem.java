package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ToolMaterial;

public class ArcaneAxeItem extends AxeItem {

    public ArcaneAxeItem(
            ToolMaterial material,
            int attackDamage,
            float attackSpeed,
            Settings settings
    ) {
        super(material, attackDamage, attackSpeed, settings);
    }
}
