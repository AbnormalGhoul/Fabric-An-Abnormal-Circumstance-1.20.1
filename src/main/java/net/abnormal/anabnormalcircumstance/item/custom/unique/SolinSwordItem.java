package net.abnormal.anabnormalcircumstance.item.custom.unique;

import dev.emi.trinkets.api.TrinketsApi;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueAbilityHelper;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SolinSwordItem extends SwordItem implements UniqueAbilityItem {
    private static final UUID DAMAGE_BOOST_ID = UUID.fromString("13fdd9f0-23c5-4c19-b4e2-8a7b50e1f00a");
    private static final EntityAttributeModifier DAMAGE_BOOST =
            new EntityAttributeModifier(DAMAGE_BOOST_ID, "Solin Dual Wield Boost", 6, EntityAttributeModifier.Operation.ADDITION);

    public SolinSwordItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;

        boolean hasMark = TrinketsApi.getTrinketComponent(player)
                .map(comp -> comp.isEquipped(ModItems.CHAMPIONS_CREST))
                .orElse(false);

        if (!hasMark) {
            player.sendMessage(
                    Text.literal("You must equip the Mark of Champion to use this weapon!")
                            .formatted(Formatting.DARK_RED),
                    true // true = action bar
            );
            return;
        }

        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(net.minecraft.text.Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)"), true);
            return;
        }
        if (!UniqueAbilityHelper.hasBothSolinWeapons(player)) {
            player.sendMessage(Text.literal("You must wield both Solin weapons").formatted(Formatting.RED), true);
            return;
        }
        World world = player.getWorld();
        // Raycast up to X blocks ahead
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d direction = player.getRotationVec(1.0F);
        Vec3d end = start.add(direction.multiply(16.0)); // max distance
        BlockHitResult hitResult = world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        ));
        Vec3d hitPos = hitResult.getType() == HitResult.Type.BLOCK
                ? hitResult.getPos().subtract(direction.multiply(1.0)) // back off slightly to avoid clipping into block
                : end;

        var targetBlockPos = net.minecraft.util.math.BlockPos.ofFloored(hitPos);
        // Ensure target position is safe (not inside a solid block)
        if (!world.getBlockState(targetBlockPos).getCollisionShape(world, targetBlockPos).isEmpty()) {
            player.sendMessage(Text.literal("Can't teleport there!").formatted(Formatting.RED), true);
            return;
        }
        // Teleport the player
        player.requestTeleport(hitPos.x, hitPos.y, hitPos.z);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 5.0f, 1.0f);

        player.sendMessage(Text.literal("Teleport!").formatted(Formatting.GOLD), true);
        UniqueItemCooldownManager.setCooldown(player, 45 * 1000);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient()) return;
        if (entity instanceof PlayerEntity player) {
            boolean bothEquipped = UniqueAbilityHelper.hasBothSolinWeapons(player);

            // Add +4.5 damage if both weapons are equipped
            var attr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (attr != null) {
                boolean hasBoost = attr.getModifier(DAMAGE_BOOST_ID) != null;
                if (bothEquipped && !hasBoost) {
                    attr.addTemporaryModifier(DAMAGE_BOOST);
                } else if (!bothEquipped && hasBoost) {
                    attr.removeModifier(DAMAGE_BOOST_ID);
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Increased damage while both Blades are held together").formatted(net.minecraft.util.Formatting.AQUA));
        tooltip.add(Text.literal("Active: Instant short teleport (R)").formatted(net.minecraft.util.Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 45s").formatted(net.minecraft.util.Formatting.GRAY));
    }
}
