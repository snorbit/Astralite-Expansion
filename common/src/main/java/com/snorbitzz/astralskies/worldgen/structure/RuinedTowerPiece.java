package com.snorbitzz.astralskies.worldgen.structure;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.StructureManager;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import com.snorbitzz.astralskies.registry.ModBlocks;

/**
 * RuinedTowerPiece — a crumbling sky tower (10×20×10).
 *
 * Features:
 *   - Tall floating stone spire with missing chunks (ruin effect)
 *   - Random crystal block accents on the outer face
 *   - A broken astralite ore cap at the top
 *   - One chest at y=8 (mid level) with ruin loot table
 */
public class RuinedTowerPiece extends StructurePiece {

    public static StructurePieceType TYPE;
    private static final int W = 10, H = 20, D = 10;

    public RuinedTowerPiece(BlockPos origin) {
        super(TYPE, 0, new BoundingBox(
                origin.getX(), origin.getY(), origin.getZ(),
                origin.getX() + W - 1,
                origin.getY() + H - 1,
                origin.getZ() + D - 1
        ));
    }

    public RuinedTowerPiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super(TYPE, tag);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {}

    @Override
    public void postProcess(WorldGenLevel level, StructureManager mgr,
                            ChunkGenerator gen, RandomSource rand,
                            BoundingBox bounds, ChunkPos chunkPos,
                            BlockPos pivot) {
        BlockState stone   = ModBlocks.FLOATING_STONE.defaultBlockState();
        BlockState crystal = ModBlocks.CRYSTAL_BLOCK.defaultBlockState();
        BlockState ore     = ModBlocks.ASTRALITE_ORE.defaultBlockState();

        // Full hollow tower
        for (int y = 0; y < H; y++) {
            for (int z = 0; z < D; z++) {
                for (int x = 0; x < W; x++) {
                    boolean isWall = x == 0 || x == W-1 || z == 0 || z == D-1;
                    if (!isWall) continue;

                    // Ruin effect: randomly skip some wall blocks above y=10
                    if (y > 10 && rand.nextInt(4) == 0) continue;
                    // Even more ruined above y=15
                    if (y > 15 && rand.nextInt(2) == 0) continue;

                    placeBlock(level, bounds, stone, x, y, z);

                    // Crystal accents
                    if (rand.nextInt(8) == 0) placeBlock(level, bounds, crystal, x, y, z);
                }
            }
        }

        // Ore cap at top
        for (int x = 2; x < W-2; x++)
            for (int z = 2; z < D-2; z++)
                placeBlock(level, bounds, ore, x, H-1, z);

        // Floor
        for (int x = 1; x < W-1; x++)
            for (int z = 1; z < D-1; z++)
                placeBlock(level, bounds, stone, x, 0, z);

        // Mid-level chest
        placeBlock(level, bounds, Blocks.CHEST.defaultBlockState(), W/2, 8, D/2);
        BlockPos chestPos = getWP(W/2, 8, D/2);
        if (bounds.isInside(chestPos) &&
                level.getBlockEntity(chestPos) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity c) {
            c.setLootTable(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "chests/ruined_tower"),
                    rand.nextLong()
            );
        }

        // Lanterns at base corners
        for (int[] p : new int[][]{{1,1},{1,D-2},{W-2,1},{W-2,D-2}})
            placeBlock(level, bounds, Blocks.LANTERN.defaultBlockState(), p[0], 1, p[1]);
    }

    private void placeBlock(WorldGenLevel level, BoundingBox bounds, BlockState state, int x, int y, int z) {
        BlockPos pos = getWP(x, y, z);
        if (bounds.isInside(pos)) level.setBlock(pos, state, 2);
    }

    private BlockPos getWP(int x, int y, int z) {
        return new BlockPos(boundingBox.minX() + x, boundingBox.minY() + y, boundingBox.minZ() + z);
    }
}
