package net.abnormal.anabnormalcircumstance.entity.client.render;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.entity.client.model.OrcWarriorModel;
import net.abnormal.anabnormalcircumstance.entity.custom.OrcWarriorEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OrcWarriorRenderer extends GeoEntityRenderer<OrcWarriorEntity> {

    public OrcWarriorRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OrcWarriorModel());
        this.shadowRadius = 0.9f;
    }

    @Override
    public Identifier getTextureLocation(OrcWarriorEntity animatable) {
        return new Identifier(AnAbnormalCircumstance.MOD_ID, "textures/entity/orc_warrior.png");
    }

    @Override
    public void render(OrcWarriorEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
