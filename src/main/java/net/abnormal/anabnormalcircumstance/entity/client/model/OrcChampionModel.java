package net.abnormal.anabnormalcircumstance.entity.client.model;

import net.abnormal.anabnormalcircumstance.entity.custom.mob.OrcChampionEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class OrcChampionModel extends GeoModel<OrcChampionEntity> {

    @Override
    public Identifier getModelResource(OrcChampionEntity object) {
        return new Identifier("anabnormalcircumstance", "geo/orc_champion.geo.json");
    }

    @Override
    public Identifier getTextureResource(OrcChampionEntity object) {
        return new Identifier("anabnormalcircumstance", "textures/entity/orc_champion.png");
    }

    @Override
    public Identifier getAnimationResource(OrcChampionEntity animatable) {
        return new Identifier("anabnormalcircumstance", "animations/orc_champion.animation.json");
    }
}
