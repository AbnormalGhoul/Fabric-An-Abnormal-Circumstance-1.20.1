package net.abnormal.anabnormalcircumstance.entity.client.model;

import net.abnormal.anabnormalcircumstance.entity.custom.mob.BroodmotherEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class BroodmotherModel extends GeoModel<BroodmotherEntity> {

    @Override
    public Identifier getModelResource(BroodmotherEntity object) {
        return new Identifier("anabnormalcircumstance", "geo/broodmother.geo.json");
    }

    @Override
    public Identifier getTextureResource(BroodmotherEntity object) {
        return new Identifier("anabnormalcircumstance", "textures/entity/broodmother.png");
    }

    @Override
    public Identifier getAnimationResource(BroodmotherEntity animatable) {
        return new Identifier("anabnormalcircumstance", "animations/broodmother.animation.json");
    }
}
