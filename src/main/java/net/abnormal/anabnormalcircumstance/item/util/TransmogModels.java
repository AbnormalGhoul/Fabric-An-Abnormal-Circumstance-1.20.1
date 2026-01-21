package net.abnormal.anabnormalcircumstance.item.util;

import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;

public final class TransmogModels {

    private static final Map<Identifier, Float> MODEL_MAP = new HashMap<>();

    static {
        // Sword Transmogs
        register("anabnormalcircumstance:crown_blade",       0.01F);
        register("anabnormalcircumstance:druids_staff",      0.02F);
        register("anabnormalcircumstance:sylvestrian_blade", 0.03F);
        register("anabnormalcircumstance:last_rose",         0.04F);
        register("anabnormalcircumstance:great_sword",       0.05F);
        register("anabnormalcircumstance:necromancer_blade", 0.06F);
        register("anabnormalcircumstance:toxic_scythe",      0.07F);
        register("anabnormalcircumstance:cataclysm",         0.08F);
        register("anabnormalcircumstance:pox_spreader",      0.09F);
        register("anabnormalcircumstance:holy_spear",        0.10F);
        register("anabnormalcircumstance:magnetite_sword",   0.11F);
        register("anabnormalcircumstance:demonic_blade",     0.12F);
        register("anabnormalcircumstance:cursed_blade",      0.13F);
        register("anabnormalcircumstance:oceanic_might",     0.14F);

        // Axe Transmogs
        register("anabnormalcircumstance:gargoyle_axe",      0.51F);
        register("anabnormalcircumstance:magma_club",        0.52F);
        register("anabnormalcircumstance:abyssal_axe",       0.53F);
        register("anabnormalcircumstance:mana_axe",          0.54F);
    }

    private static void register(String id, float value) {
        MODEL_MAP.put(new Identifier(id), value);
    }

    public static float getModelValue(Identifier id) {
        return MODEL_MAP.getOrDefault(id, 0.0F);
    }

    private TransmogModels() {}
}
