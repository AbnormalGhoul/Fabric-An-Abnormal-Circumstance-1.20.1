package net.abnormal.anabnormalcircumstance.effect;


import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {

    public static final StatusEffect BLEEDING = new BleedingEffect(StatusEffectCategory.HARMFUL, 0x8B0000);
    public static final StatusEffect STUN = new StunEffect(StatusEffectCategory.HARMFUL, 0xAAAAAA);
    public static final StatusEffect CONFUSION = new ConfuseEffect(StatusEffectCategory.HARMFUL, 0xFFFF00);

    public static void registerEffects() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier("anabnormalcircumstance", "bleeding"), BLEEDING);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("anabnormalcircumstance", "stun"), STUN);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("anabnormalcircumstance", "confusion"), CONFUSION);
    }


}
