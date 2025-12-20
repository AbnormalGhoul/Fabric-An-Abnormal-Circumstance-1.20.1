package net.abnormal.anabnormalcircumstance.datagen;

import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {

    // SPELL RUNE
    ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SPELL_RUNE, 1)
                .pattern("Q#Q")
                .pattern("YXY")
                .pattern("#Q#")
                .input('X', ModItems.BOTTLED_LIGHTNING)
                .input('#', ModItems.MANA_CLUSTER)
                .input('Y', Items.CRYING_OBSIDIAN)
                .input('Q', Items.ENDER_PEARL)
                .showNotification(true)
                .criterion("has_mana_cluster", conditionsFromItem(ModItems.MANA_CLUSTER))
                .offerTo(consumer, new Identifier(getRecipeName(ModItems.SPELL_RUNE)));

        // CALCITE
        new ShapelessRecipeJsonBuilder(RecipeCategory.MISC, Items.CALCITE, 2)
                .input(Items.COBBLESTONE)
                .input(Items.DIORITE)
                .criterion("has_diorite", conditionsFromItem(Items.DIORITE))
                .offerTo(consumer, new Identifier(getRecipeName(Items.CALCITE)));

        // PHANTOM MEMBRANE
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.PHANTOM_MEMBRANE, 1)
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .input('X', Items.LEATHER)
                .input('#', Items.FEATHER)
                .showNotification(true)
                .criterion("has_feather", conditionsFromItem(Items.FEATHER))
                .offerTo(consumer, new Identifier(getRecipeName(Items.PHANTOM_MEMBRANE)));

        // SPONGE
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.SPONGE, 4)
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .input('X', Items.PUFFERFISH)
                .input('#', Items.SAND)
                .showNotification(true)
                .criterion("has_pufferfish", conditionsFromItem(Items.PUFFERFISH))
                .offerTo(consumer, new Identifier(getRecipeName(Items.SPONGE)));

        // GREEN_DYE just cuz I feel like it
        new ShapelessRecipeJsonBuilder(RecipeCategory.MISC, Items.GREEN_DYE, 2)
                .input(Items.BLUE_DYE)
                .input(Items.YELLOW_DYE)
                .criterion("has_blue_dye", conditionsFromItem(Items.BLUE_DYE))
                .offerTo(consumer, new Identifier(getRecipeName(Items.GREEN_DYE)));

        // ECHO_SHARD
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ECHO_SHARD, 4)
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .input('X', Items.ENDER_PEARL)
                .input('#', Items.SCULK)
                .showNotification(true)
                .criterion("has_sculk", conditionsFromItem(Items.SCULK))
                .offerTo(consumer, new Identifier(getRecipeName(Items.ECHO_SHARD)));

        // HEART_OF_THE_SEA
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.HEART_OF_THE_SEA, 1)
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .input('X', Items.DIAMOND_BLOCK)
                .input('#', Items.NAUTILUS_SHELL)
                .showNotification(true)
                .criterion("has_nautilus_shell", conditionsFromItem(Items.NAUTILUS_SHELL))
                .offerTo(consumer, new Identifier(getRecipeName(Items.HEART_OF_THE_SEA)));

        // SHULKER_BOX
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.LIGHT_BLUE_SHULKER_BOX, 1)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .input('X', Items.DIAMOND_BLOCK)
                .input('#', Items.POPPED_CHORUS_FRUIT)
                .showNotification(true)
                .criterion("has_diamond_block", conditionsFromItem(Items.DIAMOND_BLOCK))
                .offerTo(consumer, new Identifier(getRecipeName(Items.LIGHT_BLUE_SHULKER_BOX)));

        // CHORUS_FRUIT
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.CHORUS_FRUIT, 4)
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .input('#', Items.APPLE)
                .input('X', Items.ENDER_PEARL)
                .showNotification(true)
                .criterion("has_apple", conditionsFromItem(Items.APPLE))
                .offerTo(consumer, new Identifier(getRecipeName(Items.CHORUS_FRUIT)));

        // ENDSTONE
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.END_STONE, 1)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .input('#', Items.COBBLESTONE)
                .input('X', Items.ENDER_PEARL)
                .showNotification(true)
                .criterion("has_ender_pearl", conditionsFromItem(Items.ENDER_PEARL))
                .offerTo(consumer, new Identifier(getRecipeName(Items.END_STONE)));

        // TOTEM OF PURITY
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TOTEM_OF_PURITY, 1)
                .pattern("Q#Q")
                .pattern("#X#")
                .pattern("Q#Q")
                .input('#', Items.GOLD_INGOT)
                .input('X', Items.DIAMOND)
                .input('Q' , Items.BLAZE_POWDER)
                .showNotification(true)
                .criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
                .offerTo(consumer, new Identifier(getRecipeName(ModItems.TOTEM_OF_PURITY)));

        // KARAMBIT
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.KARAMBIT, 1)
                .pattern("  #")
                .pattern(" X ")
                .pattern("Q  ")
                .input('#', ModItems.ORC_CHAMPION_FANG)
                .input('X', ModItems.MANA_CLUSTER)
                .input('Q' , Items.STICK)
                .showNotification(true)
                .criterion("has_orc_champion_fang", conditionsFromItem(ModItems.ORC_CHAMPION_FANG))
                .offerTo(consumer, new Identifier(getRecipeName(ModItems.KARAMBIT)));

        // WIDOWS_GEM
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.WIDOWS_GEM, 1)
                .pattern("Q#Q")
                .pattern("$X$")
                .pattern("Q#Q")
                .input('#', ModItems.ARACHNID_SILK)
                .input('X', ModItems.MANA_CLUSTER)
                .input('Q' , Items.GOLD_BLOCK)
                .input('$' , ModItems.BROODMOTHER_EGG)
                .showNotification(true)
                .criterion("has_broodmother_egg", conditionsFromItem(ModItems.BROODMOTHER_EGG))
                .offerTo(consumer, new Identifier(getRecipeName(ModItems.WIDOWS_GEM)));


        // SILVER ARROW
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.SILVER_ARROW, 8)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .input('#', Items.ARROW)
                .input('X', Items.IRON_INGOT)
                .group("arrow")
                .showNotification(true)
                .criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
                .offerTo(consumer, new Identifier(getRecipeName(ModItems.SILVER_ARROW)));

        // Mana Shard -> Mana Gem
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MANA_GEM)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .input('#', ModItems.MANA_SHARD)
                .criterion("has_mana_shard", conditionsFromItem(ModItems.MANA_SHARD))
                .offerTo(consumer);

        // Mana Gem -> Mana Cluster
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MANA_CLUSTER)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .input('#', ModItems.MANA_GEM)
                .criterion("has_mana_gem", conditionsFromItem(ModItems.MANA_GEM))
                .offerTo(consumer);

        // Mana Cluster -> Mana Core
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MANA_CORE)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .input('#', ModItems.MANA_CLUSTER)
                .criterion("has_mana_cluster", conditionsFromItem(ModItems.MANA_CLUSTER))
                .offerTo(consumer);

        // Copper Coin -> Silver Coin
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SILVER_COIN)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .input('#', ModItems.COPPER_COIN)
                .criterion("has_copper_coin", conditionsFromItem(ModItems.COPPER_COIN))
                .offerTo(consumer, new Identifier("anabnormalcircumstance", "silver_coin_from_copper"));

        // Silver Coin -> 9 Copper Coins
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COPPER_COIN, 9)
                .input(ModItems.SILVER_COIN)
                .criterion("has_silver_coin", conditionsFromItem(ModItems.SILVER_COIN))
                .offerTo(consumer, new Identifier("anabnormalcircumstance", "copper_coin_from_silver"));

        // Silver Coin -> Gold Coin
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.GOLD_COIN)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .input('#', ModItems.SILVER_COIN)
                .criterion("has_silver_coin", conditionsFromItem(ModItems.SILVER_COIN))
                .offerTo(consumer, new Identifier("anabnormalcircumstance", "gold_coin_from_silver"));

        // Gold Coin -> 9 Silver Coins
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SILVER_COIN, 9)
                .input(ModItems.GOLD_COIN)
                .criterion("has_gold_coin", conditionsFromItem(ModItems.GOLD_COIN))
                .offerTo(consumer, new Identifier("anabnormalcircumstance", "silver_coin_from_gold"));

        // Gold Coin -> Platinum Coin
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PLATINUM_COIN)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .input('#', ModItems.GOLD_COIN)
                .criterion("has_gold_coin", conditionsFromItem(ModItems.GOLD_COIN))
                .offerTo(consumer, new Identifier("anabnormalcircumstance", "platinum_coin_from_gold"));

        // Platinum Coin -> 9 Gold Coins
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.GOLD_COIN, 9)
                .input(ModItems.PLATINUM_COIN)
                .criterion("has_platinum_coin", conditionsFromItem(ModItems.PLATINUM_COIN))
                .offerTo(consumer, new Identifier("anabnormalcircumstance", "gold_coin_from_platinum"));

        // Dwarven Pickaxe
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.DWARVEN_PICKAXE)
                .pattern("QXQ")
                .pattern(" # ")
                .pattern(" # ")
                .input('X', Items.NETHER_STAR)
                .input('#', Items.NETHERITE_INGOT)
                .input('Q', ModItems.MANA_CORE)
                .criterion("has_mana_core", conditionsFromItem(ModItems.MANA_CORE))
                .offerTo(consumer, new Identifier("anabnormalcircumstance", "dwarven_pickaxe"));

        // Mana Shard
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MANA_SHARD)
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .input('#', Items.AMETHYST_SHARD)
                .input('X', Items.DIAMOND)
                .criterion("has_amethyst_shard", conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(consumer, new Identifier("anabnormalcircumstance", "mana_shard"));

        // Blank Scroll
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.SPELL_SCROLL)
                .pattern("X#X")
                .pattern("#Q#")
                .pattern("X#X")
                .input('X', Items.GLOW_INK_SAC)
                .input('#', Items.DIAMOND)
                .input('Q', Items.PAPER)
                .criterion("has_glow_ink_sac", conditionsFromItem(Items.GLOW_INK_SAC))
                .offerTo(consumer, new Identifier("anabnormalcircumstance", "spell_scroll"));


    }
}