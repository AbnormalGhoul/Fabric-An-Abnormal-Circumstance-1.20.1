package net.abnormal.anabnormalcircumstance.entity.client.render;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.entity.client.model.BroodWebProjectileModel;
import net.abnormal.anabnormalcircumstance.entity.custom.projectile.BroodWebProjectileEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BroodWebProjectileRenderer extends GeoEntityRenderer<BroodWebProjectileEntity> {

    public BroodWebProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BroodWebProjectileModel());
        this.shadowRadius = 0.2f;
    }

    @Override
    public Identifier getTextureLocation(BroodWebProjectileEntity animatable) {
        return new Identifier(AnAbnormalCircumstance.MOD_ID, "textures/entity/brood_web_projectile.png");
    }
}
