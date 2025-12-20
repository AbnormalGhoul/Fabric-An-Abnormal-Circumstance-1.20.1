package net.abnormal.anabnormalcircumstance.entity.client.render;

import net.abnormal.anabnormalcircumstance.entity.client.model.HatchetModel;
import net.abnormal.anabnormalcircumstance.entity.custom.projectile.HatchetProjectileEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HatchetRenderer extends GeoEntityRenderer<HatchetProjectileEntity> {

    public HatchetRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new HatchetModel());
        this.shadowRadius = 0.2f;
    }

    @Override
    public Identifier getTextureLocation(HatchetProjectileEntity animatable) {
        return new Identifier("anabnormalcircumstance", "textures/entity/hatchet.png");
    }

}
