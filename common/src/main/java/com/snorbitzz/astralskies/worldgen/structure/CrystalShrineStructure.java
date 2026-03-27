package com.snorbitzz.astralskies.worldgen.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.*;

import java.util.Optional;

/** Crystal Shrine structure — rare, generates in crystal biome, highest loot. */
public class CrystalShrineStructure extends Structure {

    public static final MapCodec<CrystalShrineStructure> CODEC =
            simpleCodec(CrystalShrineStructure::new);

    public CrystalShrineStructure(StructureSettings s) { super(s); }

    @Override public StructureType<?> type() { return ModStructures.CRYSTAL_SHRINE_TYPE; }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        ChunkPos cp = ctx.chunkPos();
        BlockPos origin = new BlockPos(cp.getMiddleBlockX() - 4, 140, cp.getMiddleBlockZ() - 4);
        return Optional.of(new GenerationStub(origin, b -> b.addPiece(new CrystalShrinePiece(origin))));
    }
}
