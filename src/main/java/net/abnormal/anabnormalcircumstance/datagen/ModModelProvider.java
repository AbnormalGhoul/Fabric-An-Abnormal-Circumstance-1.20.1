package net.abnormal.anabnormalcircumstance.datagen;

import net.abnormal.anabnormalcircumstance.block.ModBlocks;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DARK_SAND);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        // Weapons
        itemModelGenerator.register(ModItems.KARAMBIT, Models.HANDHELD);
        itemModelGenerator.register(ModItems.AERO_BLADE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.HYDRO_BLADE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PYRO_BLADE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.GEO_BLADE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.FORGEFATHER_JUDGMENT, Models.HANDHELD);

        // Dungeon Loot
        itemModelGenerator.register(ModItems.SILVER_NECKLACE, Models.GENERATED);
        itemModelGenerator.register(ModItems.GOLDEN_EARRINGS, Models.GENERATED);
        itemModelGenerator.register(ModItems.DIAMOND_RING, Models.GENERATED);
        itemModelGenerator.register(ModItems.RUSTED_KEY, Models.GENERATED);
        itemModelGenerator.register(ModItems.BOTTLED_LIGHTNING, Models.GENERATED);
        itemModelGenerator.register(ModItems.ORC_HIDE, Models.GENERATED);
        itemModelGenerator.register(ModItems.ORC_CHAMPION_FANG, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARACHNID_SILK, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROODMOTHER_EGG, Models.GENERATED);

        // Shrine Loot
        itemModelGenerator.register(ModItems.HYDRO_CATALYST, Models.GENERATED);
        itemModelGenerator.register(ModItems.PYRO_CATALYST, Models.GENERATED);
        itemModelGenerator.register(ModItems.GEO_CATALYST, Models.GENERATED);
        itemModelGenerator.register(ModItems.AERO_CATALYST, Models.GENERATED);

        // Consumable Essences/Souls
        itemModelGenerator.register(ModItems.HYDRO_ESSENCE, Models.GENERATED);
        itemModelGenerator.register(ModItems.PYRO_ESSENCE, Models.GENERATED);
        itemModelGenerator.register(ModItems.GEO_ESSENCE, Models.GENERATED);
        itemModelGenerator.register(ModItems.AERO_ESSENCE, Models.GENERATED);
        itemModelGenerator.register(ModItems.ORC_CHAMPION_SOUL, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROODMOTHER_SOUL, Models.GENERATED);

        // Currency
        itemModelGenerator.register(ModItems.COPPER_COIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILVER_COIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.GOLD_COIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.PLATINUM_COIN, Models.GENERATED);

        // Mana Crystals
        itemModelGenerator.register(ModItems.MANA_SHARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.MANA_GEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.MANA_CLUSTER, Models.GENERATED);
        itemModelGenerator.register(ModItems.MANA_CORE, Models.GENERATED);

        // Utility
        itemModelGenerator.register(ModItems.CLAIM_RUNE, Models.GENERATED);
        itemModelGenerator.register(ModItems.ORIGIN_RUNE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPELL_RUNE, Models.GENERATED);
        itemModelGenerator.register(ModItems.TOTEM_OF_PURITY, Models.GENERATED);

        // Scrolls
        itemModelGenerator.register(ModItems.BLANK_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.HYDRO_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.PYRO_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.GEO_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.AERO_SCROLL, Models.GENERATED);

        // Speed
        itemModelGenerator.register(ModItems.SPARK_OF_SWIFTNESS, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_LONG_SWIFTNESS, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_STRONG_SWIFTNESS, Models.GENERATED);

        // Strength
        itemModelGenerator.register(ModItems.SPARK_OF_STRENGTH, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_LONG_STRENGTH, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_STRONG_STRENGTH, Models.GENERATED);

        // Regeneration
        itemModelGenerator.register(ModItems.SPARK_OF_REGENERATION, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_LONG_REGENERATION, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_STRONG_REGENERATION, Models.GENERATED);

        // Slow Falling
        itemModelGenerator.register(ModItems.SPARK_OF_SLOW_FALLING, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_LONG_SLOW_FALLING, Models.GENERATED);

        // Invisibility
        itemModelGenerator.register(ModItems.SPARK_OF_INVISIBILITY, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_LONG_INVISIBILITY, Models.GENERATED);

        // Jump Boost
        itemModelGenerator.register(ModItems.SPARK_OF_JUMP_BOOST, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_LONG_JUMP_BOOST, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_STRONG_JUMP_BOOST, Models.GENERATED);

        // Night Vision
        itemModelGenerator.register(ModItems.SPARK_OF_LONG_NIGHT_VISION, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_NIGHT_VISION, Models.GENERATED);

        // Water Breathing
        itemModelGenerator.register(ModItems.SPARK_OF_WATER_BREATHING, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPARK_OF_LONG_WATER_BREATHING, Models.GENERATED);

        // Awkward & Water Sparks
        itemModelGenerator.register(ModItems.AWKWARD_SPARK, Models.GENERATED);
        itemModelGenerator.register(ModItems.WATER_SPARK, Models.GENERATED);

    }
}
