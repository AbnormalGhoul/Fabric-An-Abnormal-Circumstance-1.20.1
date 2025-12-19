package net.abnormal.anabnormalcircumstance.entity.client.model;

import net.abnormal.anabnormalcircumstance.entity.custom.projectile.BroodWebProjectileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class BroodWebProjectileModel extends GeoModel<BroodWebProjectileEntity> {
    @Override
    public Identifier getModelResource(BroodWebProjectileEntity object) {
        return new Identifier("anabnormalcircumstance", "geo/brood_web_projectile.geo.json");
    }

    @Override
    public Identifier getTextureResource(BroodWebProjectileEntity object) {
        return new Identifier("anabnormalcircumstance", "textures/entity/brood_web_projectile.png");
    }

    @Override
    public Identifier getAnimationResource(BroodWebProjectileEntity animatable) {
        return new Identifier("anabnormalcircumstance", "animations/brood_web_projectile.animation.json");
    }
}
