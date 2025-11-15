package net.abnormal.anabnormalcircumstance.entity.client.model;

import net.abnormal.anabnormalcircumstance.entity.custom.projectile.JavelinProjectileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class JavelinModel extends GeoModel<JavelinProjectileEntity> {

    @Override
    public Identifier getModelResource(JavelinProjectileEntity animatable) {
        return new Identifier("anabnormalcircumstance", "geo/javelin.geo.json");
    }

    @Override
    public Identifier getTextureResource(JavelinProjectileEntity animatable) {
        return new Identifier("anabnormalcircumstance", "textures/entity/javelin.png");
    }

    @Override
    public Identifier getAnimationResource(JavelinProjectileEntity animatable) {
        return new Identifier("anabnormalcircumstance", "animations/javelin.animation.json");
    }
}
