package net.abnormal.anabnormalcircumstance.entity.client.model;

import net.abnormal.anabnormalcircumstance.entity.custom.mob.BroodWarriorEntity;
import net.minecraft.util.Identifier;

import software.bernie.geckolib.model.GeoModel;

public class BroodWarriorModel extends GeoModel<BroodWarriorEntity> {

    @Override
    public Identifier getModelResource(BroodWarriorEntity object) {
        return new Identifier("anabnormalcircumstance", "geo/brood_warrior.geo.json");
    }

    @Override
    public Identifier getTextureResource(BroodWarriorEntity object) {
        return new Identifier("anabnormalcircumstance", "textures/entity/brood_warrior.png");
    }

    @Override
    public Identifier getAnimationResource(BroodWarriorEntity animatable) {
        return new Identifier("anabnormalcircumstance", "animations/brood_warrior.animation.json");
    }
}
