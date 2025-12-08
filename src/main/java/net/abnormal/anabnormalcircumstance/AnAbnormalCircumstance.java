package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.block.ModBlocks;
import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.abnormal.anabnormalcircumstance.event.PhoenixFireHandler;
import net.abnormal.anabnormalcircumstance.magic.ModSpells;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.abnormal.anabnormalcircumstance.network.StormlordsWillPacket;
import net.abnormal.anabnormalcircumstance.recipe.ModBrewingRecipes;
import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.event.ModAdvancementHandler;
import net.abnormal.anabnormalcircumstance.event.ModEvents;
import net.abnormal.anabnormalcircumstance.event.StunEventHandler;
import net.abnormal.anabnormalcircumstance.item.ModItemGroups;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.recipe.ModRecipeTypes;
import net.abnormal.anabnormalcircumstance.recipe.ModRecipes;
import net.abnormal.anabnormalcircumstance.recipe.SpellScrollRecipeSerializer;
import net.abnormal.anabnormalcircumstance.screen.ModScreenHandlers;
import net.abnormal.anabnormalcircumstance.util.FtbaCommand;
import net.abnormal.anabnormalcircumstance.util.ServerTimeCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnAbnormalCircumstance implements ModInitializer {
	public static final String MOD_ID = "anabnormalcircumstance";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
	public void onInitialize() {

        PacketHandler.register();
        StormlordsWillPacket.register();

        ModSpells.registerAll();

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModEffects.registerEffects();
        ModItemGroups.registerItemGroups();

        ModEvents.registerEvents();
        PhoenixFireHandler.register();
        ModEntities.registerModEntities();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();

        StunEventHandler.register();
        ModAdvancementHandler.register();

        ModBrewingRecipes.registerAll();
        ModRecipes.registerRecipes();
        ModRecipeTypes.register();

        ServerTimeCommand.register();

        // FTB command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FtbaCommand.register(dispatcher);
        });

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