package net.abnormal.anabnormalcircumstance.entity;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.entity.custom.mob.*;
import net.abnormal.anabnormalcircumstance.entity.custom.projectile.BroodWebProjectileEntity;
import net.abnormal.anabnormalcircumstance.entity.custom.projectile.HatchetProjectileEntity;
import net.abnormal.anabnormalcircumstance.entity.custom.projectile.JavelinProjectileEntity;
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
                    .trackRangeBlocks(4).trackedUpdateRate(20).build()
    );

    public static final EntityType<JavelinProjectileEntity> JAVELIN_PROJECTILE =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(AnAbnormalCircumstance.MOD_ID, "javelin_projectile"),
                    FabricEntityTypeBuilder.<JavelinProjectileEntity>create(SpawnGroup.MISC, JavelinProjectileEntity::new)
                            .dimensions(EntityDimensions.fixed(1.0f, 0.25f))
                            .trackRangeBlocks(64).trackedUpdateRate(10).build()
            );

    public static final EntityType<HatchetProjectileEntity> HATCHET_PROJECTILE =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(AnAbnormalCircumstance.MOD_ID, "hatchet_projectile"),
                    FabricEntityTypeBuilder.<HatchetProjectileEntity>create(SpawnGroup.MISC, HatchetProjectileEntity::new)
                            .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                            .trackRangeBlocks(64)
                            .trackedUpdateRate(10)
                            .build()
            );


    public static final EntityType<OrcWarriorEntity> ORC_WARRIOR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AnAbnormalCircumstance.MOD_ID, "orc_warrior"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, OrcWarriorEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 2.25f)).build());

    public static final EntityType<OrcChampionEntity> ORC_CHAMPION = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AnAbnormalCircumstance.MOD_ID, "orc_champion"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, OrcChampionEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 2.25f)).build());

    public static final EntityType<OrcJavelinThrowerEntity> ORC_JAVELIN_THROWER =
            Registry.register(Registries.ENTITY_TYPE,
                    new Identifier(AnAbnormalCircumstance.MOD_ID, "orc_javelin_thrower"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, OrcJavelinThrowerEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 2.25f)).build());


    public static final EntityType<BroodWarriorEntity> BROOD_WARRIOR = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AnAbnormalCircumstance.MOD_ID, "brood_warrior"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BroodWarriorEntity::new)
                    .dimensions(EntityDimensions.fixed(1.6f, 1.0f))
                    .build()
    );

    public static final EntityType<BroodWebberEntity> BROOD_WEBBER =
            Registry.register(Registries.ENTITY_TYPE, new Identifier(AnAbnormalCircumstance.MOD_ID, "brood_webber"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BroodWebberEntity::new)
                            .dimensions(EntityDimensions.fixed(1.6f, 1.0f)).build());

    public static final EntityType<BroodWebProjectileEntity> BROOD_WEB_PROJECTILE =
            Registry.register(Registries.ENTITY_TYPE, new Identifier(AnAbnormalCircumstance.MOD_ID, "brood_web_projectile"),
                    FabricEntityTypeBuilder.<BroodWebProjectileEntity>create(SpawnGroup.MISC, BroodWebProjectileEntity::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .trackRangeBlocks(64).trackedUpdateRate(10)
                            .build());

    public static final EntityType<BroodmotherEntity> BROODMOTHER =
            Registry.register(Registries.ENTITY_TYPE, new Identifier(AnAbnormalCircumstance.MOD_ID, "broodmother"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BroodmotherEntity::new)
                            .dimensions(EntityDimensions.fixed(2.0f, 1.5f)).build());


    public static void registerModEntities() {
        AnAbnormalCircumstance.LOGGER.info("Registering Mod Entities for " + AnAbnormalCircumstance.MOD_ID);

        // Orcs
        FabricDefaultAttributeRegistry.register(
                ModEntities.ORC_WARRIOR,
                OrcWarriorEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(
                ModEntities.ORC_JAVELIN_THROWER,
                OrcJavelinThrowerEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(
                ModEntities.ORC_CHAMPION,
                OrcChampionEntity.setAttributes());

        // Brood
        FabricDefaultAttributeRegistry.register(
                ModEntities.BROOD_WARRIOR,
                BroodWarriorEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(
                ModEntities.BROOD_WEBBER,
                BroodWebberEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(
                ModEntities.BROODMOTHER,
                BroodmotherEntity.setAttributes());
    }
}