package net.abnormal.anabnormalcircumstance.entity.client.render;

import net.abnormal.anabnormalcircumstance.entity.client.model.BroodWebberModel;
import net.abnormal.anabnormalcircumstance.entity.custom.mob.BroodWebberEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// BroodWebberRenderer.java
public class BroodWebberRenderer extends GeoEntityRenderer<BroodWebberEntity> {
    public BroodWebberRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BroodWebberModel());
        this.shadowRadius = 0.6f;
    }

    @Override
    public Identifier getTextureLocation(BroodWebberEntity entity) {
        return new Identifier("anabnormalcircumstance", "textures/entity/brood_webber.png");
    }
}

