package net.abnormal.anabnormalcircumstance.block.entity;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.block.ModBlocks;
import net.abnormal.anabnormalcircumstance.block.custom.HephaestusAltarBlock;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<HephaestusAltarBlockEntity> HEPHAESTUS_ALTAR_BLOCK_ENTITY_BLOCK =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AnAbnormalCircumstance.MOD_ID, "hephaestus_altar_be"),
                    BlockEntityType.Builder.create(HephaestusAltarBlockEntity::new, ModBlocks.HEPHAESTUS_ALTAR).build(null));



    public static void registerBlockEntities() {
        AnAbnormalCircumstance.LOGGER.info("Registering Block Entities for " + AnAbnormalCircumstance.MOD_ID);
    }
}
