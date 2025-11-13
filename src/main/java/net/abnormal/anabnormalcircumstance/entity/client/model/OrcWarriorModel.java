package net.abnormal.anabnormalcircumstance.entity.client.model;

import net.abnormal.anabnormalcircumstance.entity.custom.OrcWarriorEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OrcWarriorModel extends GeoModel<OrcWarriorEntity> {

    @Override
    public Identifier getModelResource(OrcWarriorEntity object) {
        return new Identifier("anabnormalcircumstance", "geo/orc_warrior.geo.json");
    }

    @Override
    public Identifier getTextureResource(OrcWarriorEntity object) {
        return new Identifier("anabnormalcircumstance", "textures/entity/orc_warrior.png");
    }

    @Override
    public Identifier getAnimationResource(OrcWarriorEntity animatable) {
        return new Identifier("anabnormalcircumstance", "animations/orc_warrior.animation.json");
    }

    @Override
    public void setCustomAnimations(OrcWarriorEntity animatable, long instanceId, AnimationState<OrcWarriorEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("hi_head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}

