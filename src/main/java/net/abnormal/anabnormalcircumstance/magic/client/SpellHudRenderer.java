package net.abnormal.anabnormalcircumstance.magic.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

// Renders mana bar and spell cooldown icons on the player's HUD.
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
        int x = screenW - 24;
        int y = screenH - 80;

        // Frame
        RenderSystem.setShaderTexture(0, MANA_FRAME);
        context.drawTexture(MANA_FRAME, x, y, 0, 0, 10, 70, 10, 70);

        // Fill
        int mana = ClientComponentAccess.getClientMana();
        float ratio = Math.max(0f, Math.min(1f, mana / 100.0f));
        int fullFillHeight = 68;
        int fillHeight = (int) (ratio * fullFillHeight);
        if (fillHeight > 0) {
            int fillY = y + (fullFillHeight) - fillHeight;

            RenderSystem.setShaderTexture(0, MANA_FILL);
            // Sample the lower portion of the fill texture so the bottom remains fixed.
            int v = fullFillHeight - fillHeight; // vertical offset into the texture
            context.drawTexture(MANA_FILL, x + 1, fillY + 1, 0, v, 8, fillHeight, 8, fullFillHeight);
        }

        // === Spell Icons ===
        if (ClientComponentAccess.hasAnySpellBound()) {
            int iconX = screenW - 50;
            int iconStartY = screenH - 24;
            int offset = 0;

            for (int i = 1; i <= 5; i++) {
                var slot = ClientComponentAccess.getSlot(i);
                if (slot == null || slot.isEmpty()) continue; // Skip empty slots (no spell bound)

                // Only show the icon corresponding to the spell's tier
                Identifier icon = new Identifier(
                        AnAbnormalCircumstance.MOD_ID,
                        "textures/gui/spells/tier" + i + "_icon.png"
                );

                RenderSystem.setShaderTexture(0, icon);
                int iy = iconStartY - offset;
                context.drawTexture(icon, iconX, iy, 0, 0, 16, 16, 16, 16);

                // Draw cooldown overlay if needed
                if (slot.cooldownTicks > 0) {
                    context.fill(iconX, iy, iconX + 16, iy + 16, 0x99000000);
                    String cdText = String.valueOf(slot.getCooldownSeconds());
                    context.drawText(client.textRenderer, cdText, iconX + 4, iy + 4, 0xFFFFFF, true);
                }

                offset += 20; // Move down for next occupied slot
            }
        }
    }
}
