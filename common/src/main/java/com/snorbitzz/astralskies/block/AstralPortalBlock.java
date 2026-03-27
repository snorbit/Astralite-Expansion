package com.snorbitzz.astralskies.block;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Astral Portal Block — placed inside a complete portal frame to teleport between
 * the overworld and the Astral Sky dimension.
 *
 * Activation:
 *   The portal frame blocks surround this block in a 3×3 ring.
 *   Right-clicking a portal frame block activates it if all 8 surrounding positions
 *   are portal frames. A pure astralite block in the center can also activate it.
 *
 * Teleportation:
 *   Overworld  → Astral Sky at corresponding X/Z, Y=200
 *   Astral Sky → Overworld at corresponding X/Z, Y=64 (or first solid ground)
 *
 * The block itself is thin/transparent (like nether portal material).
 */
public class AstralPortalBlock extends Block {

    /** ResourceKey for the Astral Sky dimension */
    public static final ResourceKey<Level> ASTRAL_DIMENSION = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "astral_sky")
    );

    public AstralPortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level,
                                               BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            teleportPlayer(serverPlayer);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Called when a player walks through (collides) the portal block.
     * Implemented via entity touch in a separate event handler (Phase 5).
     * For now, right-click teleports instantly.
     */
    public static void teleportPlayer(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        Level currentLevel = player.level();

        if (currentLevel.dimension().equals(ASTRAL_DIMENSION)) {
            // Sky → Overworld
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            if (overworld == null) return;
            double tx = player.getX();
            double tz = player.getZ();
            double ty = findSafeY(overworld, (int)tx, 72, (int)tz);
            player.teleportTo(overworld, tx, ty, tz, player.getYRot(), player.getXRot());
            player.sendSystemMessage(Component.literal(
                    "§7✦ You return from the sky..."));
        } else {
            // Overworld → Sky Dimension
            ServerLevel skyDim = server.getLevel(ASTRAL_DIMENSION);
            if (skyDim == null) {
                player.sendSystemMessage(Component.literal(
                        "§cThe Astral Sky dimension has not been generated yet. " +
                        "Try creating a new world or entering via the /execute command."));
                return;
            }
            double tx = player.getX();
            double tz = player.getZ();
            player.teleportTo(skyDim, tx, 200, tz, player.getYRot(), player.getXRot());
            player.sendSystemMessage(Component.literal(
                    "§b✦ You ascend into the Astral Skies..."));
        }
    }

    /** Finds the top-most solid Y above the given search start. */
    private static double findSafeY(ServerLevel level, int x, int startY, int z) {
        for (int y = startY; y < 256; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir()) {
                return y;
            }
        }
        return startY;
    }
}
