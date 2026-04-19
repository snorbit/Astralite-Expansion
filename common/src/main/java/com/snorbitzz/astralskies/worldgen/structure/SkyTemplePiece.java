package com.snorbitzz.astralskies.worldgen.structure;

import com.snorbitzz.astralskies.Constants;
import com.snorbitzz.astralskies.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.resources.ResourceLocation;

/**
 * SkyTemplePiece — builds the sky temple procedurally at world gen time.
 *
 * Structure layout (15×11×15):
 *   - Floating stone floor platform (3-block thick base)
 *   - Crystal Block outer walls (2 blocks thick, 5 tall)
 *   - Open courtyard with astralite ore columns
 *   - Centre chamber: 1 portal frame square + 1 chest with sky loot
 *   - Four corner towers 2 blocks taller than walls
 *   - Astral Dirt fill across the base for a garden feel
 */
public class SkyTemplePiece extends StructurePiece {

    private static final int W = 15;
    private static final int H = 11;
    private static final int D = 15;

    // We'll use a stub type key — it will be registered in ModWorldGen
    public static StructurePieceType TYPE;

    public SkyTemplePiece(BlockPos origin) {
        super(TYPE, 0, new BoundingBox(
                origin.getX(), origin.getY(), origin.getZ(),
                origin.getX() + W - 1,
                origin.getY() + H - 1,
                origin.getZ() + D - 1
        ));
    }

    public SkyTemplePiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super(TYPE, tag);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {}

    @Override
    public void postProcess(WorldGenLevel level, StructureManager mgr,
                            ChunkGenerator gen, RandomSource rand,
                            BoundingBox bounds, ChunkPos chunkPos,
                            BlockPos pivot) {
        // ─── Base platform (3 thick) ──────────────────────────────────────────
        fillLayerClamped(level, bounds, ModBlocks.FLOATING_STONE.get().defaultBlockState(), 0, 0, 0, W-1, 2, D-1);
        fillLayerClamped(level, bounds, ModBlocks.ASTRAL_DIRT.get().defaultBlockState(),    1, 3, 1, W-2, 3, D-2);

        // ─── Outer walls (crystal block, 5 tall) ─────────────────────────────
        BlockState crystal = ModBlocks.CRYSTAL_BLOCK.get().defaultBlockState();
        BlockState stone   = ModBlocks.FLOATING_STONE.get().defaultBlockState();

        // North/South walls
        for (int z : new int[]{0, D-1}) {
            fillLayerClamped(level, bounds, stone,  0, 4, z, W-1, 8, z);
            fillLayerClamped(level, bounds, crystal, 1, 4, z, W-2, 8, z);
        }
        // East/West walls
        for (int x : new int[]{0, W-1}) {
            fillLayerClamped(level, bounds, stone,  x, 4, 0, x, 8, D-1);
            fillLayerClamped(level, bounds, crystal, x, 4, 1, x, 8, D-2);
        }

        // ─── Corner towers (+2 height) ────────────────────────────────────────
        for (int[] corner : new int[][]{{0,0},{0,D-1},{W-1,0},{W-1,D-1}}) {
            fillLayerClamped(level, bounds, stone, corner[0], 4, corner[1], corner[0], 10, corner[1]);
        }

        // ─── Astralite ore columns (4 total, midpoints of walls) ─────────────
        for (int[] col : new int[][]{{7,1},{7,D-2},{1,7},{W-2,7}}) {
            fillLayerClamped(level, bounds, ModBlocks.ASTRALITE_ORE.get().defaultBlockState(),
                    col[0], 4, col[1], col[0], 7, col[1]);
        }

        // ─── Centre: portal frame ring ────────────────────────────────────────
        int cx = W/2, cz = D/2;
        BlockState frame = ModBlocks.ASTRAL_PORTAL_FRAME.get().defaultBlockState();
        // 3×3 ring at y=4, leave centre open
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                placeBlockClamped(level, bounds, frame, cx+dx, 4, cz+dz);
            }
        }

        // ─── Loot chest ───────────────────────────────────────────────────────
        placeBlockClamped(level, bounds, Blocks.CHEST.defaultBlockState(), cx, 4, cz);
        BlockPos chestPos = getWorldPos(cx, 4, cz);
        if (bounds.isInside(chestPos) && level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setLootTable(
                    net.minecraft.resources.ResourceKey.create(
                            net.minecraft.core.registries.Registries.LOOT_TABLE,
                            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "chests/sky_temple")),
                    rand.nextLong()
            );
        }

        // ─── Sky lanterns (glowstone at ceil corners) ─────────────────────────
        for (int[] p : new int[][]{{2,8,2},{2,8,D-3},{W-3,8,2},{W-3,8,D-3}}) {
            placeBlockClamped(level, bounds, Blocks.GLOWSTONE.defaultBlockState(), p[0], p[1], p[2]);
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void fillLayerClamped(WorldGenLevel level, BoundingBox bounds,
                                   BlockState state,
                                   int x1, int y1, int z1, int x2, int y2, int z2) {
        for (int y = y1; y <= y2; y++)
            for (int z = z1; z <= z2; z++)
                for (int x = x1; x <= x2; x++)
                    placeBlockClamped(level, bounds, state, x, y, z);
    }

    private void placeBlockClamped(WorldGenLevel level, BoundingBox bounds,
                                    BlockState state, int x, int y, int z) {
        BlockPos pos = getWorldPos(x, y, z);
        if (bounds.isInside(pos)) level.setBlock(pos, state, 2);
    }

    /** Converts local structure coordinates to world coordinates. */
    protected BlockPos getWorldPos(int x, int y, int z) {
        return new BlockPos(
                this.boundingBox.minX() + x,
                this.boundingBox.minY() + y,
                this.boundingBox.minZ() + z
        );
    }
}
