package com.snorbitzz.astralskies.worldgen.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

/**
 * SkyTempleStructure — the Structure definition that places SkyTemplePiece.
 *
 * Generates in all 4 sky biomes at Y = 128 (mid sky).
 * Registered in ModWorldGen and referenced by structure JSON.
 */
public class SkyTempleStructure extends Structure {

    public static final MapCodec<SkyTempleStructure> CODEC =
            simpleCodec(SkyTempleStructure::new);

    public SkyTempleStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.SKY_TEMPLE_TYPE;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        ChunkPos chunkPos = ctx.chunkPos();
        int x = chunkPos.getMiddleBlockX();
        int z = chunkPos.getMiddleBlockZ();
        int y = 128; // Fixed sky height for the temple

        BlockPos origin = new BlockPos(x - 7, y, z - 7); // centre the 15×15 footprint

        return Optional.of(new GenerationStub(origin, builder ->
                builder.addPiece(new SkyTemplePiece(origin))
        ));
    }
}
