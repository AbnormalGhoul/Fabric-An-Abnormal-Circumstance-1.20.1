package net.abnormal.anabnormalcircumstance.entity;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.entity.custom.OrcWarriorEntity;
import net.abnormal.anabnormalcircumstance.entity.custom.SilverArrowEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;

public class ModEntities {

    public static final EntityType<SilverArrowEntity> SILVER_ARROW = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("anabnormalcircumstance", "silver_arrow"),
            FabricEntityTypeBuilder.<SilverArrowEntity>create(SpawnGroup.MISC, SilverArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .trackRangeBlocks(4)
                    .trackedUpdateRate(20)
                    .build()
    );

    public static final EntityType<OrcWarriorEntity> ORC_WARRIOR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AnAbnormalCircumstance.MOD_ID, "orc_warrior"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OrcWarriorEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 1.75f)).build());

    public static void registerModEntities() {
        AnAbnormalCircumstance.LOGGER.info("Registering Mod Entities for " + AnAbnormalCircumstance.MOD_ID);
    }
}