package net.abnormal.anabnormalcircumstance.entity.client.render;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.entity.client.model.OrcJavelinThrowerModel;
import net.abnormal.anabnormalcircumstance.entity.custom.mob.OrcJavelinThrowerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OrcJavelinThrowerRenderer extends GeoEntityRenderer<OrcJavelinThrowerEntity> {

    public OrcJavelinThrowerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new OrcJavelinThrowerModel());
        this.shadowRadius = 0.9f;
    }

    @Override
    public Identifier getTextureLocation(OrcJavelinThrowerEntity animatable) {
        return new Identifier(AnAbnormalCircumstance.MOD_ID, "textures/entity/orc_javelin_thrower.png");
    }

    @Override
    public void render(OrcJavelinThrowerEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
