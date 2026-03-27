package com.snorbitzz.astralskies.item;

import com.snorbitzz.astralskies.worldgen.ModWorldGen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;
import java.util.Optional;

/**
 * Astral Compass — a navigation tool that locates nearby sky structures.
 *
 * Right-click behaviour (server-side):
 *   - Searches for the nearest sky structure (Sky Temple first, then Ruined Tower, then Crystal Shrine)
 *     within a 200-chunk radius.
 *   - Reports distance in blocks and cardinal direction to the player.
 *   - Also works in the overworld to find where to build a portal.
 *
 * Has a 5-second cooldown so it can't be spammed.
 */
public class AstralCompassItem extends Item {

    private static final int COOLDOWN = 100; // 5 seconds
    private static final int SEARCH_RADIUS = 200; // chunks

    public AstralCompassItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            BlockPos playerPos = player.blockPosition();

            // Try to locate nearest sky temple first, then others
            String[] structureIds = {"sky_temple", "ruined_tower", "crystal_shrine"};
            String[] structureNames = {"Sky Temple", "Ruined Tower", "Crystal Shrine"};
            boolean found = false;

            for (int i = 0; i < structureIds.length; i++) {
                var structureKey = net.minecraft.resources.ResourceKey.create(
                        net.minecraft.core.registries.Registries.STRUCTURE,
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                "astral_skies", structureIds[i])
                );
                var structureHolder = serverLevel.registryAccess()
                        .registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE)
                        .getHolder(structureKey);

                if (structureHolder.isEmpty()) continue;

                var result = serverLevel.getChunkSource().getGenerator()
                        .findNearestMapStructure(serverLevel, structureHolder.get(),
                                playerPos, SEARCH_RADIUS, false);

                if (result != null) {
                    BlockPos structPos = result.getFirst();
                    double dist = playerPos.distSqr(structPos);
                    int blockDist = (int) Math.sqrt(dist);

                    String dir = getDirection(playerPos, structPos);
                    player.sendSystemMessage(Component.literal(
                            "§b✦ Astral Compass: §f" + structureNames[i] +
                            " §7— §f" + blockDist + " blocks §7to the §f" + dir));
                    player.sendSystemMessage(Component.literal(
                            "§8  Coords: §7[" + structPos.getX() + ", " + structPos.getY() + ", " + structPos.getZ() + "]"));
                    found = true;
                    break;
                }
            }

            if (!found) {
                player.sendSystemMessage(Component.literal(
                        "§c✦ Astral Compass: §7No sky structures found nearby. " +
                        "Try exploring further into the sky dimension."));
            }
        }

        player.getCooldowns().addCooldown(this, COOLDOWN);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /**
     * Returns rough cardinal direction from one pos to another.
     */
    private String getDirection(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dz = to.getZ() - from.getZ();

        if (Math.abs(dx) > Math.abs(dz) * 2) return dx > 0 ? "East" : "West";
        if (Math.abs(dz) > Math.abs(dx) * 2) return dz > 0 ? "South" : "North";

        String ns = dz > 0 ? "South" : "North";
        String ew = dx > 0 ? "East" : "West";
        return ns + "-" + ew;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§bLocates nearby sky structures."));
        tooltip.add(Component.literal("§7Right-click to search (5s cooldown)."));
        tooltip.add(Component.literal("§8Finds: Sky Temples, Ruined Towers, Crystal Shrines"));
    }
}
