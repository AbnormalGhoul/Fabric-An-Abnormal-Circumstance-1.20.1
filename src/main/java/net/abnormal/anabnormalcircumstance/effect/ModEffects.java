package net.abnormal.anabnormalcircumstance.effect;

import net.abnormal.anabnormalcircumstance.effect.custom.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {

    public static final StatusEffect BLEEDING = new BleedingEffect(StatusEffectCategory.HARMFUL, 0x8B0000);
    public static final StatusEffect STUN = new StunEffect(StatusEffectCategory.HARMFUL, 0xAAAAAA);
    public static final StatusEffect CONFUSION = new ConfuseEffect(StatusEffectCategory.HARMFUL, 0xFFFF00);
    public static final StatusEffect BITTEN = new BittenEffect(StatusEffectCategory.HARMFUL, 0x5A0A0A);
    public static final StatusEffect PHOENIX_FIRE = new PhoenixFireEffect(StatusEffectCategory.BENEFICIAL, 0xFF4500);
    public static final StatusEffect VULNERABILITY = new VulnerabilityEffect(StatusEffectCategory.HARMFUL, 0x8B0000);
    public static final StatusEffect RAGE = new RageEffect(StatusEffectCategory.BENEFICIAL, 0x800080);

    public static void registerEffects() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier("anabnormalcircumstance", "bleeding"), BLEEDING);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("anabnormalcircumstance", "stun"), STUN);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("anabnormalcircumstance", "confusion"), CONFUSION);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("anabnormalcircumstance", "bitten"), BITTEN);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("anabnormalcircumstance", "phoenix_fire"), PHOENIX_FIRE);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("anabnormalcircumstance", "vulnerability"), VULNERABILITY);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("anabnormalcircumstance", "rage"), RAGE);
    }
}
