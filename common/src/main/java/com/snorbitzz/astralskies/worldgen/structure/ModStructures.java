package com.snorbitzz.astralskies.worldgen.structure;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureType;

/**
 * ModStructures — registers the StructureType entries for all 3 sky structures.
 *
 * Each StructureType links a name to a Structure subclass codec.
 * The actual placement frequencies are controlled by JSON in:
 *   data/astral_skies/worldgen/structure/<name>.json
 *   data/astral_skies/worldgen/structure_set/<name>.json
 */
public class ModStructures {

    public static StructureType<SkyTempleStructure> SKY_TEMPLE_TYPE;
    public static StructureType<RuinedTowerStructure> RUINED_TOWER_TYPE;
    public static StructureType<CrystalShrineStructure> CRYSTAL_SHRINE_TYPE;

    public static void init() {
        SKY_TEMPLE_TYPE = register("sky_temple", SkyTempleStructure.CODEC);
        RUINED_TOWER_TYPE = register("ruined_tower", RuinedTowerStructure.CODEC);
        CRYSTAL_SHRINE_TYPE = register("crystal_shrine", CrystalShrineStructure.CODEC);

        // Wire piece types
        SkyTemplePiece.TYPE = registerPiece("sky_temple_piece",
                ctx -> new SkyTemplePiece(ctx, ctx.tag()));
        RuinedTowerPiece.TYPE = registerPiece("ruined_tower_piece",
                ctx -> new RuinedTowerPiece(ctx, ctx.tag()));
        CrystalShrinePiece.TYPE = registerPiece("crystal_shrine_piece",
                ctx -> new CrystalShrinePiece(ctx, ctx.tag()));

        Constants.LOG.info("Astral Skies structures registered");
    }

    private static <S extends net.minecraft.world.level.levelgen.structure.Structure>
    StructureType<S> register(String name, com.mojang.serialization.MapCodec<S> codec) {
        return Registry.register(BuiltInRegistries.STRUCTURE_TYPE,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name),
                () -> codec);
    }

    private static net.minecraft.world.level.levelgen.structure.StructurePieceType registerPiece(
            String name,
            net.minecraft.world.level.levelgen.structure.StructurePieceType type) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PIECE,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), type);
    }
}
