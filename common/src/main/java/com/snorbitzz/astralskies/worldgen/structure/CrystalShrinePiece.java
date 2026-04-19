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
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import com.snorbitzz.astralskies.registry.ModBlocks;

/**
 * CrystalShrinePiece — a small sacred crystal altar (9×9×9).
 *
 * Features:
 *   - Crystal block platform
 *   - Four crystal pillars rising 7 tall
 *   - Central altar: astralite block pedestal with glowstone atop
 *   - Chest under the altar with rare shrine loot
 *   - Purple tinted atmosphere (blue glass roof)
 */
public class CrystalShrinePiece extends StructurePiece {

    public static StructurePieceType TYPE;
    private static final int W = 9, H = 9, D = 9;

    public CrystalShrinePiece(BlockPos origin) {
        super(TYPE, 0, new BoundingBox(
                origin.getX(), origin.getY(), origin.getZ(),
                origin.getX() + W - 1,
                origin.getY() + H - 1,
                origin.getZ() + D - 1
        ));
    }

    public CrystalShrinePiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super(TYPE, tag);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {}

    @Override
    public void postProcess(WorldGenLevel level, StructureManager mgr,
                            ChunkGenerator gen, RandomSource rand,
                            BoundingBox bounds, ChunkPos chunkPos,
                            BlockPos pivot) {
        BlockState crystal   = ModBlocks.CRYSTAL_BLOCK.get().defaultBlockState();
        BlockState astralite = ModBlocks.ASTRALITE_BLOCK.get().defaultBlockState();
        BlockState stone     = ModBlocks.FLOATING_STONE.get().defaultBlockState();
        BlockState glass     = Blocks.BLUE_STAINED_GLASS.defaultBlockState();

        // Platform floor
        for (int x = 0; x < W; x++)
            for (int z = 0; z < D; z++)
                place(level, bounds, crystal, x, 0, z);

        // Four crystal pillars
        for (int[] p : new int[][]{{1,1},{1,D-2},{W-2,1},{W-2,D-2}})
            for (int y = 1; y < 8; y++)
                place(level, bounds, crystal, p[0], y, p[1]);

        // Roof (blue glass)
        for (int x = 0; x < W; x++)
            for (int z = 0; z < D; z++)
                place(level, bounds, glass, x, 8, z);

        // Central altar: astralite pedestal
        place(level, bounds, astralite, W/2, 1, D/2);
        place(level, bounds, astralite, W/2, 2, D/2);
        place(level, bounds, Blocks.GLOWSTONE.defaultBlockState(), W/2, 3, D/2);

        // Chest beneath altar
        place(level, bounds, Blocks.CHEST.defaultBlockState(), W/2, 1, D/2-1);
        BlockPos chestPos = getWP(W/2, 1, D/2-1);
        if (bounds.isInside(chestPos) &&
                level.getBlockEntity(chestPos) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity c) {
            c.setLootTable(
                    net.minecraft.resources.ResourceKey.create(
                            net.minecraft.core.registries.Registries.LOOT_TABLE,
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "chests/crystal_shrine")),
                    rand.nextLong()
            );
        }

        // Crystal candles
        for (int[] p : new int[][]{{3,3},{3,D-4},{W-4,3},{W-4,D-4}})
            place(level, bounds, Blocks.PURPLE_CANDLE.defaultBlockState(), p[0], 1, p[1]);
    }

    private void place(WorldGenLevel level, BoundingBox bounds, BlockState state, int x, int y, int z) {
        BlockPos pos = getWP(x, y, z);
        if (bounds.isInside(pos)) level.setBlock(pos, state, 2);
    }

    protected BlockPos getWP(int x, int y, int z) {
        return new BlockPos(boundingBox.minX() + x, boundingBox.minY() + y, boundingBox.minZ() + z);
    }
}
