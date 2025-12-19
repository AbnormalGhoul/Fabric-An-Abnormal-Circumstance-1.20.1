package net.abnormal.anabnormalcircumstance.entity.client.model;

import net.abnormal.anabnormalcircumstance.entity.custom.mob.BroodWebberEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

// BroodWebberModel.java
public class BroodWebberModel extends GeoModel<BroodWebberEntity> {
    @Override
    public Identifier getModelResource(BroodWebberEntity object) {
        return new Identifier("anabnormalcircumstance", "geo/brood_webber.geo.json");
    }

    @Override
    public Identifier getTextureResource(BroodWebberEntity object) {
        return new Identifier("anabnormalcircumstance", "textures/entity/brood_webber.png");
    }

    @Override
    public Identifier getAnimationResource(BroodWebberEntity animatable) {
        return new Identifier("anabnormalcircumstance", "animations/brood_webber.animation.json");
    }
}

