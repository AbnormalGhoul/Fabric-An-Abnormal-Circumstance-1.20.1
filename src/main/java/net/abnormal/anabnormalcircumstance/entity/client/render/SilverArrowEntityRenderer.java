package net.abnormal.anabnormalcircumstance.entity.client.render;

import net.abnormal.anabnormalcircumstance.entity.custom.projectile.SilverArrowProjectileEntity;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class SilverArrowEntityRenderer extends ProjectileEntityRenderer<SilverArrowProjectileEntity> {
    public SilverArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }


    @Override
    public Identifier getTexture(SilverArrowProjectileEntity entity) {
        return new Identifier("anabnormalcircumstance", "textures/entity/silver_arrow.png");
    }
}