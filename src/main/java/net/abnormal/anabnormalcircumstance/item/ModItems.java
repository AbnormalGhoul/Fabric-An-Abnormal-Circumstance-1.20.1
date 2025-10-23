package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.item.custom.*;
import net.abnormal.anabnormalcircumstance.item.custom.unique.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    // Scrolls:
    public static final Item SPELL_SCROLL = registerItem("spell_scroll", new SpellScrollItem(new Item.Settings().maxCount(64)));
    public static final Item SPELL_RUNE   = registerItem("spell_rune", new SpellRuneItem(new Item.Settings().maxCount(16)));



    // Weapons
    public static final Item KARAMBIT = registerItem("karambit",
            new KarambitItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item SILVER_ARROW = registerItem("silver_arrow", new SilverArrowItem(new Item.Settings()));

    // Blades
    public static final Item AERO_BLADE = registerItem("aero_blade", new AeroBladeItem(
                ModToolMaterials.UNIQUE, // Tool Material
                8,                       // Attack damage bonus
                -2.4f,                   // Attack speed
                new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
            ));
    public static final Item GEO_BLADE = registerItem("geo_blade", new GeoBladeItem(
            ModToolMaterials.UNIQUE,
            8,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
    ));
    public static final Item HYDRO_BLADE = registerItem("hydro_blade", new HydroBladeItem(
            ModToolMaterials.UNIQUE,
            8,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
    ));
    public static final Item PYRO_BLADE = registerItem("pyro_blade", new PyroBladeItem(
            ModToolMaterials.UNIQUE,
            8,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
    ));

    // Champion Weapons
    public static final Item FIRST_LEAF = registerItem("first_leaf", new FirstLeafBowItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item ICICLE_SHARD  = registerItem("icicle_shard", new IcicleShardItem(new Item.Settings().maxCount(1).maxDamage(2500).rarity(Rarity.EPIC), 100, 13, Items.NETHERITE_INGOT));
    public static final Item FORGEFATHER_JUDGMENT = registerItem("forgefather_judgment", new ForgefatherJudgmentItem(
            ModToolMaterials.UNIQUE,
            11,
            -3f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
    ));
    public static final Item WILL_BREAKER = registerItem("will_breaker", new WillBreakerItem(
            ModToolMaterials.UNIQUE,
            9,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
    ));
    public static final Item BLACK_SOLIN_BLADE = registerItem("black_solin_blade", new SolinSwordItem(
            ModToolMaterials.UNIQUE,
            9,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
    ));
    public static final Item WHITE_SOLIN_BLADE = registerItem("white_solin_blade", new SolinAxeItem(
            ModToolMaterials.UNIQUE,
            11,
            -3f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
    ));
    public static final Item REEDTHORN = registerItem("reedthorn", new ReedthornItem(
            ModToolMaterials.UNIQUE,
            10,
            -2.6f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
    ));



    // Dungeon Loot
    public static final Item SILVER_NECKLACE = registerItem("silver_necklace", new Item(new Item.Settings()));
    public static final Item GOLDEN_EARRINGS = registerItem("golden_earrings", new Item(new Item.Settings()));
    public static final Item DIAMOND_RING = registerItem("diamond_ring", new Item(new Item.Settings()));
    public static final Item RUSTED_KEY = registerItem("rusted_key", new Item(new Item.Settings()));
    public static final Item BOTTLED_LIGHTNING = registerItem("bottled_lightning", new Item(new Item.Settings()));
    public static final Item ORC_HIDE = registerItem("orc_hide", new Item(new Item.Settings()));
    public static final Item ORC_CHAMPION_FANG = registerItem("orc_champion_fang", new Item(new Item.Settings()));
    public static final Item ARACHNID_SILK = registerItem("arachnid_silk", new Item(new Item.Settings()));
    public static final Item BROODMOTHER_EGG = registerItem("broodmother_egg", new Item(new Item.Settings()));

    // Shrine Loot
    public static final Item HYDRO_CATALYST = registerItem("hydro_catalyst", new Item(new Item.Settings()));
    public static final Item PYRO_CATALYST = registerItem("pyro_catalyst", new Item(new Item.Settings()));
    public static final Item GEO_CATALYST = registerItem("geo_catalyst", new Item(new Item.Settings()));
    public static final Item AERO_CATALYST = registerItem("aero_catalyst", new Item(new Item.Settings()));

    // Consumable Essences/Souls
    public static final Item HYDRO_ESSENCE = registerItem("hydro_essence", new SoundFoodItem(ModFoodComponents.HYDRO_ESSENCE, new Item.Settings().maxCount(16).rarity(Rarity.EPIC)));
    public static final Item PYRO_ESSENCE = registerItem("pyro_essence", new SoundFoodItem(ModFoodComponents.PYRO_ESSENCE, new Item.Settings().maxCount(16).rarity(Rarity.EPIC)));
    public static final Item GEO_ESSENCE = registerItem("geo_essence", new SoundFoodItem(ModFoodComponents.GEO_ESSENCE, new Item.Settings().maxCount(16).rarity(Rarity.EPIC)));
    public static final Item AERO_ESSENCE = registerItem("aero_essence", new SoundFoodItem(ModFoodComponents.AERO_ESSENCE, new Item.Settings().maxCount(16).rarity(Rarity.EPIC)));

    public static final Item ORC_CHAMPION_SOUL = registerItem("orc_champion_soul", new SoundFoodItem(ModFoodComponents.ORC_CHAMPION_SOUL, new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));
    public static final Item BROODMOTHER_SOUL = registerItem("broodmother_soul", new SoundFoodItem(ModFoodComponents.BROODMOTHER_SOUL, new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));
    // Currency
    public static final Item COPPER_COIN = registerItem("copper_coin", new Item(new Item.Settings()));
    public static final Item SILVER_COIN = registerItem("silver_coin", new Item(new Item.Settings()));
    public static final Item GOLD_COIN = registerItem("gold_coin", new Item(new Item.Settings()));
    public static final Item PLATINUM_COIN = registerItem("platinum_coin", new Item(new Item.Settings()));

    // Mana Crystals
    public static final Item MANA_SHARD = registerItem("mana_shard", new Item(new Item.Settings()));
    public static final Item MANA_GEM = registerItem("mana_gem", new Item(new Item.Settings()));
    public static final Item MANA_CLUSTER = registerItem("mana_cluster", new Item(new Item.Settings()));
    public static final Item MANA_CORE = registerItem("mana_core", new Item(new Item.Settings()));

    // Utility
    public static final Item CLAIM_RUNE = registerItem("claim_rune", new Item(new Item.Settings()));
    public static final Item ORIGIN_RUNE = registerItem("origin_rune", new Item(new Item.Settings()));
    public static final Item TOTEM_OF_PURITY = registerItem("totem_of_purity", new VillagerTotemItem(new Item.Settings().maxCount(16)));
    public static final Item BLANK_SCROLL = registerItem("blank_scroll", new Item(new Item.Settings()));


    // Regular Spark
    public static final Item WATER_SPARK = registerItem("water_spark",  new Item(new Item.Settings().maxCount(16)));
    public static final Item AWKWARD_SPARK = registerItem("awkward_spark", new Item(new Item.Settings().maxCount(16)));

    // Swiftness
    public static final Item SPARK_OF_SWIFTNESS = registerItem("spark_of_swiftness",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.SPEED, 180 * 20, 0));
    public static final Item SPARK_OF_LONG_SWIFTNESS = registerItem("spark_of_long_swiftness",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.SPEED, 480 * 20, 0));
    public static final Item SPARK_OF_STRONG_SWIFTNESS = registerItem("spark_of_strong_swiftness",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.SPEED, 90 * 20, 1));

    // Strength
    public static final Item SPARK_OF_STRENGTH = registerItem("spark_of_strength",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.STRENGTH, 180 * 20, 0));
    public static final Item SPARK_OF_LONG_STRENGTH = registerItem("spark_of_long_strength",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.STRENGTH, 480 * 20, 0));
    public static final Item SPARK_OF_STRONG_STRENGTH = registerItem("spark_of_strong_strength",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.STRENGTH, 90 * 20, 1));

    // Regeneration
    public static final Item SPARK_OF_REGENERATION = registerItem("spark_of_regeneration",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.REGENERATION, 45 * 20, 0));
    public static final Item SPARK_OF_LONG_REGENERATION = registerItem("spark_of_long_regeneration",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.REGENERATION, 90 * 20, 0));
    public static final Item SPARK_OF_STRONG_REGENERATION = registerItem("spark_of_strong_regeneration",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.REGENERATION, 22 * 20 + 10, 1));  // ~22.5s → 22s + 10 ticks

    // Slow Falling
    public static final Item SPARK_OF_SLOW_FALLING = registerItem("spark_of_slow_falling",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.SLOW_FALLING, 90 * 20, 0));
    public static final Item SPARK_OF_LONG_SLOW_FALLING = registerItem("spark_of_long_slow_falling",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.SLOW_FALLING, 240 * 20, 0));

    // Invisibility
    public static final Item SPARK_OF_INVISIBILITY = registerItem("spark_of_invisibility",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.INVISIBILITY, 180 * 20, 0));
    public static final Item SPARK_OF_LONG_INVISIBILITY = registerItem("spark_of_long_invisibility",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.INVISIBILITY, 480 * 20, 0));

    // Jump Boost
    public static final Item SPARK_OF_JUMP_BOOST = registerItem("spark_of_jump_boost",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.JUMP_BOOST, 180 * 20, 0));
    public static final Item SPARK_OF_LONG_JUMP_BOOST = registerItem("spark_of_long_jump_boost",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.JUMP_BOOST, 480 * 20, 0));
    public static final Item SPARK_OF_STRONG_JUMP_BOOST = registerItem("spark_of_strong_jump_boost",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.JUMP_BOOST, 90 * 20, 1));

    // Water Breathing
    public static final Item SPARK_OF_WATER_BREATHING = registerItem("spark_of_water_breathing",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.WATER_BREATHING, 180 * 20, 0));
    public static final Item SPARK_OF_LONG_WATER_BREATHING = registerItem("spark_of_long_water_breathing",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.WATER_BREATHING, 480 * 20, 0));

    // Night Vision
    public static final Item SPARK_OF_NIGHT_VISION = registerItem("spark_of_night_vision",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.NIGHT_VISION, 180 * 20, 0));
    public static final Item SPARK_OF_LONG_NIGHT_VISION = registerItem("spark_of_long_night_vision",
            new SparkItem(new FabricItemSettings().maxCount(16),
                    StatusEffects.NIGHT_VISION, 480 * 20, 0));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(AnAbnormalCircumstance.MOD_ID, name), item);
    }

    public static void registerModItems() {
        AnAbnormalCircumstance.LOGGER.info("Registering Mod Items for " + AnAbnormalCircumstance.MOD_ID);
    }
}
