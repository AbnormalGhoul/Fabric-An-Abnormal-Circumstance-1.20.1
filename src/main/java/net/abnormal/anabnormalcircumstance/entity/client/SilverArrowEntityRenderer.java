package net.abnormal.anabnormalcircumstance.entity.client;

import net.abnormal.anabnormalcircumstance.entity.custom.SilverArrowEntity;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class SilverArrowEntityRenderer extends ProjectileEntityRenderer<SilverArrowEntity> {
    public SilverArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }


    @Override
    public Identifier getTexture(SilverArrowEntity entity) {
        return new Identifier("anabnormalcircumstance", "textures/entity/silver_arrow.png");
    }
}