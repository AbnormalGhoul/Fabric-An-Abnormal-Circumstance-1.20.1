package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.block.ModBlocks;
import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.abnormal.anabnormalcircumstance.recipe.ModBrewingRecipes;
import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.event.ModAdvancementHandler;
import net.abnormal.anabnormalcircumstance.event.ModEvents;
import net.abnormal.anabnormalcircumstance.event.StunEventHandler;
import net.abnormal.anabnormalcircumstance.item.ModItemGroups;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.recipe.ModRecipes;
import net.abnormal.anabnormalcircumstance.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnAbnormalCircumstance implements ModInitializer {
	public static final String MOD_ID = "anabnormalcircumstance";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModEffects.registerEffects();
        ModItemGroups.registerItemGroups();

        ModEvents.registerEvents();
        ModEntities.registerModEntities();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();

        StunEventHandler.register();
        ModAdvancementHandler.register();
        ModBrewingRecipes.registerAll();
        ModRecipes.registerRecipes();

        // Register unique ability packet
        ServerPlayNetworking.registerGlobalReceiver(
                new Identifier(MOD_ID, "unique_item_ability"),
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        // Handle activation for any item implementing the interface
                        if (player.getMainHandStack().getItem() instanceof UniqueAbilityItem unique)
                            unique.useUniqueAbility(player);
                        else if (player.getOffHandStack().getItem() instanceof UniqueAbilityItem unique)
                            unique.useUniqueAbility(player);
                    });
                }
        );

        LOGGER.info("An Abnormal Circumstance Mod Initialized");

    }
}