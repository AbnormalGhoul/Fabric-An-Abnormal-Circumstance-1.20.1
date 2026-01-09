package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class TransmogModels {

    // Float values must be stable and unique
    private static final Map<Identifier, Float> MODEL_MAP = new HashMap<>();

    static {
        register("anabnormalcircumstance:crown_blade",        0.1F);
        register("anabnormalcircumstance:last_rose",          0.2F);
        register("anabnormalcircumstance:gargoyle_axe",       0.3F);
        register("anabnormalcircumstance:great_sword",        0.4F);
        register("anabnormalcircumstance:necromancer_blade",  0.5F);
        register("anabnormalcircumstance:red_hammer",         0.6F);
        register("anabnormalcircumstance:toxic_scythe",       0.7F);
    }

    private static void register(String id, float value) {
        MODEL_MAP.put(new Identifier(id), value);
    }

    public static float getModelValue(Identifier id) {
        return MODEL_MAP.getOrDefault(id, 0.0F);
    }

    private TransmogModels() {}
}
