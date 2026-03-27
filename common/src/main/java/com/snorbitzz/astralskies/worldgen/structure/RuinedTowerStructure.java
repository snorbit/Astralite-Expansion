package com.snorbitzz.astralskies.worldgen.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

/** Ruined Tower structure — generates in storm/ruined biomes. */
public class RuinedTowerStructure extends Structure {

    public static final MapCodec<RuinedTowerStructure> CODEC =
            simpleCodec(RuinedTowerStructure::new);

    public RuinedTowerStructure(StructureSettings s) { super(s); }

    @Override public StructureType<?> type() { return ModStructures.RUINED_TOWER_TYPE; }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        ChunkPos cp = ctx.chunkPos();
        BlockPos origin = new BlockPos(cp.getMiddleBlockX() - 5, 96, cp.getMiddleBlockZ() - 5);
        return Optional.of(new GenerationStub(origin, b -> b.addPiece(new RuinedTowerPiece(origin))));
    }
}
