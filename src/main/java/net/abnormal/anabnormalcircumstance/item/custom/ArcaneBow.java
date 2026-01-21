package net.abnormal.anabnormalcircumstance.item.custom;

import net.fabric_extras.ranged_weapon.api.CustomBow;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.minecraft.recipe.Ingredient;


public class ArcaneBow extends CustomBow {
    @SuppressWarnings("deprecation")
    public ArcaneBow(Settings settings) {
        super(settings, () -> Ingredient.EMPTY);

        this.config(new RangedConfig(
                15,   // pull time
                8.0F,   // damage
                3.0F  // projectile velocity
        ));
    }
}
