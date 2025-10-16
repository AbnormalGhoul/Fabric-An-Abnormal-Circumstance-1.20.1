package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.block.ModBlocks;
import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.abnormal.anabnormalcircumstance.brewing.CustomBrewingRecipes;
import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.event.ModAdvancementHandler;
import net.abnormal.anabnormalcircumstance.event.ModEvents;
import net.abnormal.anabnormalcircumstance.item.ModItemGroups;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.recipe.ModRecipes;
import net.abnormal.anabnormalcircumstance.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnAbnormalCircumstance implements ModInitializer {
	public static final String MOD_ID = "anabnormalcircumstance";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

        ModItemGroups.registerItemGroups();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();

        ModEffects.registerEffects();
        ModEvents.registerEvents();

        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();

        ModAdvancementHandler.register();
        CustomBrewingRecipes.registerAll();
//        ModRecipes.registerRecipes();

        LOGGER.info("An Abnormal Circumstance Mod Initialized");

    }
}