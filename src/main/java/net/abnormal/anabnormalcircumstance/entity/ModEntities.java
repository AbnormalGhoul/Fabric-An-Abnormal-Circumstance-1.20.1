package net.abnormal.anabnormalcircumstance.entity;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.entity.custom.projectile.JavelinProjectileEntity;
import net.abnormal.anabnormalcircumstance.entity.custom.mob.OrcJavelinThrowerEntity;
import net.abnormal.anabnormalcircumstance.entity.custom.mob.OrcWarriorEntity;
import net.abnormal.anabnormalcircumstance.entity.custom.projectile.SilverArrowProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;

public class ModEntities {

    public static final EntityType<SilverArrowProjectileEntity> SILVER_ARROW = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AnAbnormalCircumstance.MOD_ID, "silver_arrow_projectile"),
            FabricEntityTypeBuilder.<SilverArrowProjectileEntity>create(SpawnGroup.MISC, SilverArrowProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .trackRangeBlocks(4)
                    .trackedUpdateRate(20)
                    .build()
    );

    public static final EntityType<JavelinProjectileEntity> JAVELIN_PROJECTILE =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(AnAbnormalCircumstance.MOD_ID, "javelin_projectile"),
                    FabricEntityTypeBuilder.<JavelinProjectileEntity>create(SpawnGroup.MISC, JavelinProjectileEntity::new)
                            .dimensions(EntityDimensions.fixed(1.0f, 0.25f))
                            .trackRangeBlocks(64)
                            .trackedUpdateRate(10)
                            .build()
            );

    public static final EntityType<OrcWarriorEntity> ORC_WARRIOR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AnAbnormalCircumstance.MOD_ID, "orc_warrior"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OrcWarriorEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 2.25f)).build());

    public static final EntityType<OrcJavelinThrowerEntity> ORC_JAVELIN_THROWER =
            Registry.register(Registries.ENTITY_TYPE,
                    new Identifier(AnAbnormalCircumstance.MOD_ID, "orc_javelin_thrower"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, OrcJavelinThrowerEntity::new)
                            .dimensions(EntityDimensions.fixed(1.0f, 2.25f))
                            .build());


    public static void registerModEntities() {
        AnAbnormalCircumstance.LOGGER.info("Registering Mod Entities for " + AnAbnormalCircumstance.MOD_ID);

        // Orcs
        FabricDefaultAttributeRegistry.register(
                ModEntities.ORC_WARRIOR,
                OrcWarriorEntity.setAttributes()
        );
        FabricDefaultAttributeRegistry.register(
                ModEntities.ORC_JAVELIN_THROWER,
                OrcJavelinThrowerEntity.createAttributes());


    }
}