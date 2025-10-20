package net.abnormal.anabnormalcircumstance.block.entity.renderer;

import net.abnormal.anabnormalcircumstance.block.entity.custom.HephaestusAltarBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

import java.lang.reflect.Method;

@Environment(EnvType.CLIENT)
@SuppressWarnings("unchecked")
public class HephaestusAltarBlockEntityRenderer implements BlockEntityRenderer<HephaestusAltarBlockEntity> {

    public HephaestusAltarBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) { }

    @Override
    public void render(HephaestusAltarBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity == null) return;
        ItemStack stack = entity.getItems().get(4); // center output slot
        if (stack.isEmpty()) return;

        matrices.push();
        try {
            // Position + scale + rotation
            matrices.translate(0.5D, 1.5D, 0.5D);
            float scale = 0.75f;
            matrices.scale(scale, scale, scale);

            World world = entity.getWorld();
            float angleDegrees = 0f;
            if (world != null) {
                long time = world.getTime();
                angleDegrees = ((time % 360L) + tickDelta) * 2f;
            } else {
                angleDegrees = tickDelta * 360f;
            }
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angleDegrees));

            // Render the item using a robust reflective call that maps params by type.
            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            Method renderMethod = findRenderItemMethod(itemRenderer.getClass());
            if (renderMethod != null) {
                Object[] args = buildArgsForMethod(renderMethod.getParameterTypes(), stack, matrices, vertexConsumers, light, overlay);
                renderMethod.invoke(itemRenderer, args);
            }
        } catch (Throwable t) {
            // swallow to avoid crashing renderer; matrix stack will be popped below
        } finally {
            matrices.pop();
        }
    }

    // Build argument array by matching parameter types to our known values.
    private static Object[] buildArgsForMethod(Class<?>[] paramTypes, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Object[] args = new Object[paramTypes.length];
        boolean usedLight = false;
        boolean usedOverlay = false;
        boolean usedSeed = false;

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> p = paramTypes[i];
            if (ItemStack.class.isAssignableFrom(p)) {
                args[i] = stack;
            } else if (MatrixStack.class.isAssignableFrom(p)) {
                args[i] = matrices;
            } else if (VertexConsumerProvider.class.isAssignableFrom(p)) {
                args[i] = vertexConsumers;
            } else if (p == int.class || p == Integer.class) {
                // choose sensible ordering: first int -> light, next int -> overlay, last int -> seed (0)
                if (!usedLight) {
                    args[i] = light;
                    usedLight = true;
                } else if (!usedOverlay) {
                    args[i] = overlay;
                    usedOverlay = true;
                } else {
                    args[i] = 0;
                    usedSeed = true;
                }
            } else if (p.isEnum()) {
                // try to find a "FIXED" constant commonly used for block-placed item rendering
                Object enumVal = null;
                try {
                    enumVal = Enum.valueOf((Class<? extends Enum>) p.asSubclass(Enum.class), "FIXED");
                } catch (Exception ignored) { }
                if (enumVal == null) {
                    Object[] constants = p.getEnumConstants();
                    if (constants != null && constants.length > 0) enumVal = constants[0];
                }
                args[i] = enumVal;
            } else {
                // fallback: null
                args[i] = null;
            }
        }

        return args;
    }

    // Find a renderItem method that looks usable (prefers methods that include MatrixStack + VertexConsumerProvider).
    private static Method findRenderItemMethod(Class<?> cls) {
        for (Method m : cls.getMethods()) {
            if (!m.getName().equals("renderItem")) continue;
            Class<?>[] p = m.getParameterTypes();
            boolean hasMatrix = false;
            boolean hasVertexProvider = false;
            for (Class<?> t : p) {
                if (MatrixStack.class.isAssignableFrom(t)) hasMatrix = true;
                if (VertexConsumerProvider.class.isAssignableFrom(t)) hasVertexProvider = true;
            }
            if (hasMatrix && hasVertexProvider && p.length >= 5 && p.length <= 8) {
                return m;
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static void register(net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry registry) {
        BlockEntityRendererRegistry.register(net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities.HEPHAESTUS_ALTAR_BLOCK_ENTITY_BLOCK, HephaestusAltarBlockEntityRenderer::new);
    }
}