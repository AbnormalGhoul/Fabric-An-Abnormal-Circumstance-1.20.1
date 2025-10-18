package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup ABNORMAL_Trinkets = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnAbnormalCircumstance.MOD_ID, "abnormal_trinkets_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.abnormal_trinkets_group"))
                    .icon(() -> new ItemStack(ModItems.HYDRO_CATALYST)).entries((displayContext, entries) -> {

                        // Blocks
                        entries.add(ModBlocks.HEPHAESTUS_ALTAR);
                        entries.add(ModBlocks.DARK_SAND);

                        // Weapons
                        entries.add(ModItems.KARAMBIT);
                        entries.add(ModItems.SILVER_ARROW);

                        // Dungeon Loot
                        entries.add(ModItems.SILVER_NECKLACE);
                        entries.add(ModItems.GOLDEN_EARRINGS);
                        entries.add(ModItems.DIAMOND_RING);
                        entries.add(ModItems.RUSTED_KEY);
                        entries.add(ModItems.BOTTLED_LIGHTNING);
                        entries.add(ModItems.ORC_HIDE);
                        entries.add(ModItems.ORC_CHAMPION_FANG);
                        entries.add(ModItems.ARACHNID_SILK);
                        entries.add(ModItems.BROODMOTHER_EGG);

                        // Shrine Loot
                        entries.add(ModItems.HYDRO_CATALYST);
                        entries.add(ModItems.PYRO_CATALYST);
                        entries.add(ModItems.GEO_CATALYST);
                        entries.add(ModItems.AERO_CATALYST);

                        // Consumable Essences/Souls
                        entries.add(ModItems.HYDRO_ESSENCE);
                        entries.add(ModItems.PYRO_ESSENCE);
                        entries.add(ModItems.GEO_ESSENCE);
                        entries.add(ModItems.AERO_ESSENCE);
                        entries.add(ModItems.ORC_CHAMPION_SOUL);
                        entries.add(ModItems.BROODMOTHER_SOUL);

                        // Currency
                        entries.add(ModItems.COPPER_COIN);
                        entries.add(ModItems.SILVER_COIN);
                        entries.add(ModItems.GOLD_COIN);
                        entries.add(ModItems.PLATINUM_COIN);

                        // Mana Crystals
                        entries.add(ModItems.MANA_SHARD);
                        entries.add(ModItems.MANA_GEM);
                        entries.add(ModItems.MANA_CLUSTER);
                        entries.add(ModItems.MANA_CORE);

                        // Utility
                        entries.add(ModItems.CLAIM_RUNE);
                        entries.add(ModItems.ORIGIN_RUNE);
                        entries.add(ModItems.SPELL_RUNE);
                        entries.add(ModItems.TOTEM_OF_PURITY);

                        // Scrolls
                        entries.add(ModItems.BLANK_SCROLL);
                        entries.add(ModItems.HYDRO_SCROLL);
                        entries.add(ModItems.PYRO_SCROLL);
                        entries.add(ModItems.GEO_SCROLL);
                        entries.add(ModItems.AERO_SCROLL);

                        // Blades
                        entries.add(ModItems.AERO_BLADE);
                        entries.add(ModItems.HYDRO_BLADE);
                        entries.add(ModItems.PYRO_BLADE);
                        entries.add(ModItems.GEO_BLADE);

                    }).build());

    public static final ItemGroup SPARKS = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnAbnormalCircumstance.MOD_ID, "sparks_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.sparks_group"))
                    .icon(() -> new ItemStack(ModItems.SPARK_OF_REGENERATION)).entries((displayContext, entries) -> {

                        entries.add(ModItems.WATER_SPARK);
                        entries.add(ModItems.AWKWARD_SPARK);

                        entries.add(ModItems.SPARK_OF_SWIFTNESS);
                        entries.add(ModItems.SPARK_OF_LONG_SWIFTNESS);
                        entries.add(ModItems.SPARK_OF_STRONG_SWIFTNESS);

                        entries.add(ModItems.SPARK_OF_STRENGTH);
                        entries.add(ModItems.SPARK_OF_LONG_STRENGTH);
                        entries.add(ModItems.SPARK_OF_STRONG_STRENGTH);

                        entries.add(ModItems.SPARK_OF_REGENERATION);
                        entries.add(ModItems.SPARK_OF_LONG_REGENERATION);
                        entries.add(ModItems.SPARK_OF_STRONG_REGENERATION);

                        entries.add(ModItems.SPARK_OF_SLOW_FALLING);
                        entries.add(ModItems.SPARK_OF_LONG_SLOW_FALLING);

                        entries.add(ModItems.SPARK_OF_INVISIBILITY);
                        entries.add(ModItems.SPARK_OF_LONG_INVISIBILITY);

                        entries.add(ModItems.SPARK_OF_JUMP_BOOST);
                        entries.add(ModItems.SPARK_OF_LONG_JUMP_BOOST);
                        entries.add(ModItems.SPARK_OF_STRONG_JUMP_BOOST);

                        entries.add(ModItems.SPARK_OF_NIGHT_VISION);
                        entries.add(ModItems.SPARK_OF_LONG_NIGHT_VISION);

                        entries.add(ModItems.SPARK_OF_WATER_BREATHING);
                        entries.add(ModItems.SPARK_OF_LONG_WATER_BREATHING);

                    }).build());

    public static void registerItemGroups() {
        AnAbnormalCircumstance.LOGGER.info("Registering Mod Item Groups for " + AnAbnormalCircumstance.MOD_ID);
    }
}
