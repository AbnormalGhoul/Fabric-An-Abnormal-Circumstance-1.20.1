package net.abnormal.anabnormalcircumstance.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import net.minecraft.advancement.PlayerAdvancementTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Redirect the PlayerManager.broadcast call in PlayerAdvancementTracker.grantCriterion(...)
// so we can suppress normal (task) advancement announcements.
@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    /**
     * Redirect the call PlayerManager.broadcast(Text, boolean) inside
     * grantCriterion(Advancement, String).
     *
     * Parameters:
     *  - playerManager : the PlayerManager instance (the original target)
     *  - text : the Text that would be broadcast
     *  - shouldFilter : the boolean argument passed to broadcast (usually false)
     *  - advancement : the Advancement parameter from the calling grantCriterion method
     *
     * The extra 'Advancement advancement' parameter is allowed because it's a parameter
     * of the calling method; Mixin will pass it through.
     */
    @Redirect(
            method = "grantCriterion(Lnet/minecraft/advancement/Advancement;Ljava/lang/String;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
            )
    )
    private void redirectAdvancementBroadcast(PlayerManager playerManager, Text text, boolean shouldFilter, Advancement advancement) {
        AdvancementDisplay display = advancement.getDisplay();
        if (display != null) {
            // The frame id used in chat.type.advancement.<id> is "task", "goal", or "challenge".
            // We suppress only "task" (normal advancements).
            String frameId = display.getFrame().getId();
            if ("task".equals(frameId)) {
                // Skip broadcasting normal (task) advancements
                return;
            }
        }

        // Otherwise, perform normal broadcast
        playerManager.broadcast(text, shouldFilter);
    }
}
