package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.block.ModBlocks;
import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.abnormal.anabnormalcircumstance.enchantment.ModEnchantments;
import net.abnormal.anabnormalcircumstance.event.custom.PhoenixFireHandler;
import net.abnormal.anabnormalcircumstance.magic.ModSpells;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.abnormal.anabnormalcircumstance.recipe.ModBrewingRecipes;
import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.event.custom.ModAdvancementHandler;
import net.abnormal.anabnormalcircumstance.event.ModEvents;
import net.abnormal.anabnormalcircumstance.event.custom.StunEventHandler;
import net.abnormal.anabnormalcircumstance.item.ModItemGroups;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.recipe.ModRecipeTypes;
import net.abnormal.anabnormalcircumstance.recipe.ModRecipes;
import net.abnormal.anabnormalcircumstance.screen.ModScreenHandlers;
import net.abnormal.anabnormalcircumstance.util.ServerTimeCommand;
import net.abnormal.anabnormalcircumstance.util.WitchDropModifier;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnAbnormalCircumstance implements ModInitializer {
	public static final String MOD_ID = "anabnormalcircumstance";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
	public void onInitialize() {

        PacketHandler.register();
        ModSpells.registerAll();
        ModEvents.registerEvents();

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModItemGroups.registerItemGroups();

        ModEnchantments.register();
        ModEffects.registerEffects();
        ModEntities.registerModEntities();

        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();

        ModAdvancementHandler.register();
        ModBrewingRecipes.registerAll();
        WitchDropModifier.register();

        ServerTimeCommand.register();
        ModRecipes.registerRecipes();
        ModRecipeTypes.register();

        LOGGER.info("An Abnormal Circumstance Mod Initialized");

    }
}