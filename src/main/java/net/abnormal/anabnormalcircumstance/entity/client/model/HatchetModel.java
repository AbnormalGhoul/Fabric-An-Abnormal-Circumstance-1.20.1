package net.abnormal.anabnormalcircumstance.entity.client.model;

import net.abnormal.anabnormalcircumstance.entity.custom.projectile.HatchetProjectileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HatchetModel extends GeoModel<HatchetProjectileEntity> {

    @Override
    public Identifier getModelResource(HatchetProjectileEntity animatable) {
        return new Identifier("anabnormalcircumstance", "geo/hatchet.geo.json");
    }

    @Override
    public Identifier getTextureResource(HatchetProjectileEntity animatable) {
        return new Identifier("anabnormalcircumstance", "textures/entity/hatchet.png");
    }

    @Override
    public Identifier getAnimationResource(HatchetProjectileEntity animatable) {
        return new Identifier("anabnormalcircumstance", "animations/hatchet.animation.json");
    }
}
