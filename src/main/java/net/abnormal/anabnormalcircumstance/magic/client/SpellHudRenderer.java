package net.abnormal.anabnormalcircumstance.magic.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Renders mana bar and spell cooldown icons on the player's HUD.
 */
public class SpellHudRenderer implements HudRenderCallback {

    private static final Identifier MANA_FRAME = new Identifier(AnAbnormalCircumstance.MOD_ID, "textures/gui/mana_bar_frame.png");
    private static final Identifier MANA_FILL  = new Identifier(AnAbnormalCircumstance.MOD_ID, "textures/gui/mana_bar_fill.png");

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();

        // === Mana Bar ===
        int x = 10;
        int hotbarY = screenH - 22;
        int y = hotbarY - 64;

        // Frame
        RenderSystem.setShaderTexture(0, MANA_FRAME);
        context.drawTexture(MANA_FRAME, x, y, 0, 0, 16, 64, 16, 64);

        // Fill
        int mana = ClientComponentAccess.getClientMana();
        float ratio = Math.max(0f, Math.min(1f, mana / 100.0f));
        int fillHeight = (int) (ratio * 60.0f);
        int fillY = y + (64 - 2) - fillHeight;

        RenderSystem.setShaderTexture(0, MANA_FILL);
        context.drawTexture(MANA_FILL, x + 2, fillY, 0, 0, 12, fillHeight, 12, 60);

        // === Spell Icons ===
        int iconX = screenW - 24;
        int iconStartY = hotbarY - 4;
        int offset = 0;

        for (int i = 1; i <= 5; i++) {
            Identifier icon = new Identifier(AnAbnormalCircumstance.MOD_ID, "textures/gui/spells/tier" + i + "_icon.png");
            RenderSystem.setShaderTexture(0, icon);
            int iy = iconStartY - offset;
            context.drawTexture(icon, iconX, iy, 0, 0, 16, 16, 16, 16);

            var slot = ClientComponentAccess.getSlot(i);
            if (slot != null && slot.cooldownTicks > 0) {
                // Cooldown overlay
                context.fill(iconX, iy, iconX + 16, iy + 16, 0x99000000);
                String cdText = String.valueOf(slot.cooldownSeconds());
                context.drawText(client.textRenderer, cdText, iconX + 4, iy + 4, 0xFFFFFF, true);
            }
            offset += 20;
        }
    }
}
