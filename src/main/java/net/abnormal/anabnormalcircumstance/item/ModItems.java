package net.abnormal.anabnormalcircumstance.item;

import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.item.custom.*;
import net.abnormal.anabnormalcircumstance.item.unique.*;
import net.abnormal.anabnormalcircumstance.item.util.ModArmorMaterials;
import net.abnormal.anabnormalcircumstance.item.util.ModFoodComponents;
import net.abnormal.anabnormalcircumstance.item.util.ModToolMaterials;
import net.abnormal.anabnormalcircumstance.item.util.SoundFoodItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.List;

public class ModItems {

    // Pendants
    public static final Item NIGHT_PENDANT = Registry.register(Registries.ITEM,
            new Identifier("anabnormalcircumstance", "night_pendant"),
            new PendantItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof())
    );
    public static final Item SHROOM_PENDANT = Registry.register(Registries.ITEM,
            new Identifier("anabnormalcircumstance", "shroom_pendant"),
            new PendantItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof())
    );
    public static final Item SOULBOUND_PENDANT = Registry.register(Registries.ITEM,
            new Identifier("anabnormalcircumstance", "soulbound_pendant"),
            new PendantItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof())
    );
    public static final Item FOREST_PENDANT = Registry.register(Registries.ITEM,
            new Identifier("anabnormalcircumstance", "forest_pendant"),
            new PendantItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof())
    );
    public static final Item RAIN_PENDANT = Registry.register(Registries.ITEM,
            new Identifier("anabnormalcircumstance", "rain_pendant"),
            new PendantItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof())
    );
    public static final Item ALLY_PENDANT = Registry.register(Registries.ITEM,
            new Identifier("anabnormalcircumstance", "ally_pendant"),
            new PendantItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof())
    );

    // Unique Items
    public static final Item BERSERKERS_VIAL = Registry.register(Registries.ITEM,
            new Identifier("anabnormalcircumstance", "berserker_vial"),
            new BerserkersVialItem(new Item.Settings().rarity(Rarity.RARE).fireproof().maxCount(1))
    );
    public static final Item PRISMATIC_STAFF = Registry.register(Registries.ITEM,
            new Identifier("anabnormalcircumstance", "prismatic_staff"),
            new PrismaticStaffItem(new Item.Settings().rarity(Rarity.RARE).fireproof().maxCount(1))
    );
    public static final Item VARNIA_STAFF = Registry.register(Registries.ITEM,
            new Identifier("anabnormalcircumstance", "varnia_staff"),
            new VarniaStaffItem(new Item.Settings().rarity(Rarity.RARE).fireproof().maxCount(1))
    );

    // Trinkets
    public static final Item WIDOWS_GEM = Registry.register(Registries.ITEM,
            new Identifier("anabnormalcircumstance", "widows_gem"),
            new WidowsGemItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof())
    );
    public static final Item BROOD_GEM = Registry.register(
            Registries.ITEM,
            new Identifier("anabnormalcircumstance", "brood_gem"),
            new BroodGemItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE))
    );
    static {
        TrinketsApi.registerTrinket(BROOD_GEM, (Trinket) BROOD_GEM);
        TrinketsApi.registerTrinket(WIDOWS_GEM, (Trinket) WIDOWS_GEM);
    }

    // Crests
    public static final Item CHAMPIONS_CREST = Registry.register(
            Registries.ITEM,
            new Identifier("anabnormalcircumstance", "champions_crest"),
            new ChampionsCrestItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof())
    );
    public static final Item LEADERS_CREST = Registry.register(
            Registries.ITEM,
            new Identifier("anabnormalcircumstance", "leaders_crest"),
            new LeadersCrestItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof())
    );

    // Spawn Eggs
    public static final Item ORC_WARRIOR_SPAWN_EGG = registerItem("orc_warrior_spawn_egg",
            new SpawnEggItem(ModEntities.ORC_WARRIOR, 0x15940a, 0x8b4513,
                    new FabricItemSettings()));

    public static final Item ORC_JAVELIN_THROWER_SPAWN_EGG = registerItem("orc_javelin_thrower_spawn_egg",
            new SpawnEggItem(ModEntities.ORC_JAVELIN_THROWER, 0x15940a, 0xC0C0C0,
                    new FabricItemSettings()));

    public static final Item ORC_CHAMPION_SPAWN_EGG = registerItem("orc_champion_spawn_egg",
            new SpawnEggItem(ModEntities.ORC_CHAMPION, 0x15940a, 0xD3AF37,
                    new FabricItemSettings()));

    public static final Item BROOD_WARRIOR_SPAWN_EGG = registerItem("brood_warrior_spawn_egg",
            new SpawnEggItem(ModEntities.BROOD_WARRIOR, 0x453b4a, 0xF92A53,
                    new FabricItemSettings()));

    public static final Item BROOD_WEBBER_SPAWN_EGG = registerItem("brood_webber_spawn_egg",
            new SpawnEggItem(ModEntities.BROOD_WEBBER, 0x453b4a, 0xf3f0db,
                    new FabricItemSettings()));

    public static final Item BROODMOTHER_SPAWN_EGG = registerItem("broodmother_spawn_egg",
            new SpawnEggItem(ModEntities.BROODMOTHER, 0x453b4a, 0xD3AF37,
                    new FabricItemSettings()));

    // Magic Items
    public static final Item SPELL_SCROLL = registerItem("spell_scroll", new SpellScrollItem(new Item.Settings().maxCount(16)));
    public static final Item SPELL_RUNE   = registerItem("spell_rune", new SpellRuneItem(new Item.Settings()));
    public static final Item SILVER_ARROW = registerItem("silver_arrow", new SilverArrowItem(new Item.Settings()));

    // Weapons
    public static final Item KARAMBIT = registerItem("karambit",
            new KarambitItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE).fireproof()));

    public static final Item EXTENDO_GRIP = registerItem("extendo_grip",
            new ExtendoGripItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE).fireproof()));

    // Blades
    public static final Item DWARVEN_PICKAXE = registerItem("dwarven_pickaxe", new DwarvenPickaxeItem(
            ModToolMaterials.UNIQUE,
            9,
            -2.8f,
            new FabricItemSettings().fireproof().rarity(Rarity.RARE)));
    public static final Item AERO_BLADE = registerItem("aero_blade", new AeroBladeItem(
                ModToolMaterials.UNIQUE,
                8,
                -2.4f,
                new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()
            ));
    public static final Item GEO_BLADE = registerItem("geo_blade", new GeoBladeItem(
            ModToolMaterials.UNIQUE,
            10,
            -3.0f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()
    ));
    public static final Item HYDRO_BLADE = registerItem("hydro_blade", new HydroBladeItem(
            ModToolMaterials.UNIQUE,
            8,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()
    ));
    public static final Item PYRO_BLADE = registerItem("pyro_blade", new PyroBladeItem(
            ModToolMaterials.UNIQUE,
            8,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()
    ));
    public static final Item ARCANE_BLADE = registerItem("arcane_blade", new ArcaneBladeItem(
            ModToolMaterials.UNIQUE,
            8,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof()
    ));
    public static final Item ARCANE_AXE = registerItem("arcane_axe", new ArcaneAxeItem(
            ModToolMaterials.UNIQUE,
            10,
            -3.0f,
            new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof()
    ));
    public static final Item ARCANE_BOW = registerItem("arcane_bow", new ArcaneBow(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE).fireproof().maxDamage(750)));

    // Champion Weapons
    public static final Item ICICLE_SHARD = registerItem("icicle_shard", new IcicleShardItem(
                    SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
                    () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
                    List.of(), new FabricItemSettings().maxCount(1).maxDamage(2500).rarity(Rarity.EPIC).fireproof()
    ));
    public static final Item LAST_LEAF = registerItem("last_leaf", new LastLeafItem(
            ModToolMaterials.UNIQUE,
            9,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()
    ));
    public static final Item FIRST_LEAF = registerItem("first_leaf", new FirstLeafBowItem(
            new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC).fireproof()
    ));
    public static final Item FORGEFATHER_JUDGMENT = registerItem("forgefather_judgment", new ForgefatherJudgmentItem(
            ModToolMaterials.UNIQUE,
            11,
            -3.0f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()
    ));
    public static final Item WILL_BREAKER = registerItem("will_breaker", new WillBreakerItem(
            ModToolMaterials.UNIQUE,
            9,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()
    ));
    public static final Item BLACK_SOLIN_BLADE = registerItem("black_solin_blade", new SolinSwordItem(
            ModToolMaterials.UNIQUE,
            9,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()
    ));
    public static final Item WHITE_SOLIN_BLADE = registerItem("white_solin_blade", new SolinAxeItem(
            ModToolMaterials.UNIQUE,
            9,
            -2.4f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()
    ));
    public static final Item REEDTHORN = registerItem("reedthorn", new ReedthornItem(
            ModToolMaterials.UNIQUE,
            10,
            -2.6f,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()
    ));

    // Carcan Item
    public static final Item CRAB_CLAW = registerItem("crab_claw", new Item(new Item.Settings().maxCount(1)));

    // Ingots
    public static final Item ADAMANTITE_INGOT = registerItem("adamantite_ingot", new Item(new Item.Settings().fireproof()));
    public static final Item MITHRIL_INGOT = registerItem("mithril_ingot", new Item(new Item.Settings().fireproof()));

    // Armors
    public static final Item ADAMANTITE_HELMET = registerItem("adamantite_helmet",
            new AdamantiteArmorItem(ModArmorMaterials.ADAMANTITE, ArmorItem.Type.HELMET, new FabricItemSettings().fireproof()));
    public static final Item ADAMANTITE_CHESTPLATE = registerItem("adamantite_chestplate",
            new AdamantiteArmorItem(ModArmorMaterials.ADAMANTITE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().fireproof()));
    public static final Item ADAMANTITE_LEGGINGS = registerItem("adamantite_leggings",
            new AdamantiteArmorItem(ModArmorMaterials.ADAMANTITE, ArmorItem.Type.LEGGINGS, new FabricItemSettings().fireproof()));
    public static final Item ADAMANTITE_BOOTS = registerItem("adamantite_boots",
            new AdamantiteArmorItem(ModArmorMaterials.ADAMANTITE, ArmorItem.Type.BOOTS, new FabricItemSettings().fireproof()));

    public static final Item MITHRIL_HELMET = registerItem("mithril_helmet",
            new MithrilArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.HELMET, new FabricItemSettings().fireproof()));
    public static final Item MITHRIL_CHESTPLATE = registerItem("mithril_chestplate",
            new MithrilArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().fireproof()));
    public static final Item MITHRIL_LEGGINGS = registerItem("mithril_leggings",
            new MithrilArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.LEGGINGS, new FabricItemSettings().fireproof()));
    public static final Item MITHRIL_BOOTS = registerItem("mithril_boots",
            new MithrilArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.BOOTS, new FabricItemSettings().fireproof()));

    // Supporter Items
    public static final Item VULKAN_BLADE = registerItem("vulkan_blade", new Item(new Item.Settings().maxCount(1)));
    public static final Item CRIMSON_BIDENT = registerItem("crimson_bident", new Item(new Item.Settings().maxCount(1)));
    public static final Item CROWN_BLADE = registerItem("crown_blade", new Item(new Item.Settings().maxCount(1)));
    public static final Item DRUIDS_STAFF = registerItem("druid_staff", new Item(new Item.Settings().maxCount(1)));
    public static final Item SYLVESTRIAN_BLADE = registerItem("sylvestrian_blade", new Item(new Item.Settings().maxCount(1)));

    // Donator Items
    public static final Item LAST_ROSE = registerItem("last_rose", new Item(new Item.Settings().maxCount(1)));
    public static final Item GARGOYLE_AXE = registerItem("gargoyle_axe", new Item(new Item.Settings().maxCount(1)));
    public static final Item GREAT_SWORD = registerItem("great_sword", new Item(new Item.Settings().maxCount(1)));
    public static final Item NECROMANCER_SWORD = registerItem("necromancer_blade", new Item(new Item.Settings().maxCount(1)));
    public static final Item MAGMA_CLUB = registerItem("magma_club", new Item(new Item.Settings().maxCount(1)));
    public static final Item TOXIC_SCYTHE = registerItem("toxic_scythe", new Item(new Item.Settings().maxCount(1)));
    public static final Item CATACLYSM = registerItem("cataclysm", new Item(new Item.Settings().maxCount(1)));
    public static final Item POX_SPREADER = registerItem("pox_spreader", new Item(new Item.Settings().maxCount(1)));
    public static final Item HOLY_SPEAR = registerItem("holy_spear", new Item(new Item.Settings().maxCount(1)));
    public static final Item MAGNETITE_SWORD = registerItem("magnetite_sword", new Item(new Item.Settings().maxCount(1)));
    public static final Item DEMONIC_BLADE = registerItem("demonic_blade", new Item(new Item.Settings().maxCount(1)));
    public static final Item ABYSSAL_AXE = registerItem("abyssal_axe", new Item(new Item.Settings().maxCount(1)));
    public static final Item CURSED_BLADE = registerItem("cursed_blade", new Item(new Item.Settings().maxCount(1)));
    public static final Item MANA_AXE = registerItem("mana_axe", new Item(new Item.Settings().maxCount(1)));
    public static final Item OCEANIC_MIGHT = registerItem("oceanic_might", new Item(new Item.Settings().maxCount(1)));
    public static final Item FLOWERING_MADNESS = registerItem("flowering_madness", new Item(new Item.Settings().maxCount(1)));
    public static final Item FIRE_MACE = registerItem("fire_mace", new Item(new Item.Settings().maxCount(1)));
    public static final Item BATTLE_STANDARD = registerItem("battle_standard", new Item(new Item.Settings().maxCount(1)));
    public static final Item WITCH_SCYTHE = registerItem("witch_scythe", new Item(new Item.Settings().maxCount(1)));
    public static final Item ROGUE_DAGGER = registerItem("rogue_dagger", new Item(new Item.Settings().maxCount(1)));
    public static final Item HELLSPAWN_AXE = registerItem("hellspawn_axe", new Item(new Item.Settings().maxCount(1)));
    public static final Item FORGE_HAMMER = registerItem("forge_hammer", new Item(new Item.Settings().maxCount(1)));
    public static final Item DEATH_GRIP = registerItem("death_grip", new Item(new Item.Settings().maxCount(1)));
    public static final Item SOUL_STEEL_HATCHET = registerItem("soul_steel_hatchet", new Item(new Item.Settings().maxCount(1)));
    public static final Item DARK_MOON = registerItem("dark_moon", new Item(new Item.Settings().maxCount(1)));
    public static final Item MANA_SPEAR = registerItem("mana_spear", new Item(new Item.Settings().maxCount(1)));
    public static final Item BEECOMB_SWORD = registerItem("beecomb_sword", new Item(new Item.Settings().maxCount(1)));
    // Donator Bows
    public static final Item ARACHNID_BOW = registerItem("arachnid_bow", new BowItem(new Item.Settings().maxCount(1)));
    public static final Item BASALT_BOW = registerItem("basalt_bow", new BowItem(new Item.Settings().maxCount(1)));
    public static final Item ETERNAL_BOW = registerItem("eternal_bow", new BowItem(new Item.Settings().maxCount(1)));

    // Arcane Items
    public static final Item ARCANE_ESSENCE = registerItem("arcane_essence", new Item(new Item.Settings().fireproof()));
    public static final Item ARCANE_UPGRADE = registerItem("arcane_upgrade", new Item(new Item.Settings().fireproof()));

    // Dungeon Loot
    public static final Item SILVER_NECKLACE = registerItem("silver_necklace", new Item(new Item.Settings()));
    public static final Item GOLDEN_EARRINGS = registerItem("golden_earrings", new Item(new Item.Settings()));
    public static final Item DIAMOND_RING = registerItem("diamond_ring", new Item(new Item.Settings()));

    // Other Loot
    public static final Item BOTTLED_LIGHTNING = registerItem("bottled_lightning", new Item(new Item.Settings()));
    public static final Item RUSTED_KEY = registerItem("rusted_key", new Item(new Item.Settings()));

    // Orc Loot
    public static final Item ORC_HIDE = registerItem("orc_hide", new Item(new Item.Settings()));
    public static final Item ORC_CHAMPION_FANG = registerItem("orc_champion_fang", new Item(new Item.Settings()));

    // Brood Loot
    public static final Item ARACHNID_SILK = registerItem("arachnid_silk", new Item(new Item.Settings()));
    public static final Item BROODMOTHER_EGG = registerItem("broodmother_egg", new Item(new Item.Settings()));

    // Catalysts
    public static final Item HYDRO_CATALYST = registerItem("hydro_catalyst", new Item(new Item.Settings()));
    public static final Item PYRO_CATALYST = registerItem("pyro_catalyst", new Item(new Item.Settings()));
    public static final Item GEO_CATALYST = registerItem("geo_catalyst", new Item(new Item.Settings()));
    public static final Item AERO_CATALYST = registerItem("aero_catalyst", new Item(new Item.Settings()));

    // Essences
    public static final Item HYDRO_ESSENCE = registerItem("hydro_essence", new SoundFoodItem(ModFoodComponents.HYDRO_ESSENCE, new Item.Settings().maxCount(16).rarity(Rarity.EPIC)));
    public static final Item PYRO_ESSENCE = registerItem("pyro_essence", new SoundFoodItem(ModFoodComponents.PYRO_ESSENCE, new Item.Settings().maxCount(16).rarity(Rarity.EPIC)));
    public static final Item GEO_ESSENCE = registerItem("geo_essence", new SoundFoodItem(ModFoodComponents.GEO_ESSENCE, new Item.Settings().maxCount(16).rarity(Rarity.EPIC)));
    public static final Item AERO_ESSENCE = registerItem("aero_essence", new SoundFoodItem(ModFoodComponents.AERO_ESSENCE, new Item.Settings().maxCount(16).rarity(Rarity.EPIC)));

    // Souls
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
    public static final Item CLAIM_RUNE = registerItem("claim_rune", new ClaimRuneItem(new Item.Settings()));
    public static final Item ORIGIN_RUNE = registerItem("origin_rune", new OriginRuneItem(new Item.Settings()));
    public static final Item NATION_RUNE = registerItem("nation_rune", new NationRuneItem(new Item.Settings()));
    public static final Item TRANSMOG_TOKEN = registerItem("transmog_token", new TransmogTokenItem(new Item.Settings().fireproof().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item ABNORMAL_RELIC = registerItem("abnormal_relic", new AbnormalRelicItem(new Item.Settings().fireproof().rarity(Rarity.EPIC)));
    public static final Item TOTEM_OF_PURITY = registerItem("totem_of_purity", new VillagerTotemItem(new Item.Settings().maxCount(16)));


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
                    StatusEffects.REGENERATION, 22 * 20 + 10, 1));

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
