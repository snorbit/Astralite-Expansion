package com.snorbitzz.astralskies.worldgen;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * Holds ResourceKeys for all Astral Skies worldgen objects (biomes, features, dimension).
 * Actual registration is data-driven (JSON), so this class just provides typed keys
 * to reference these objects safely from Java code.
 */
public class ModWorldGen {

    // ─── Biomes ───────────────────────────────────────────────────────────────

    public static final ResourceKey<net.minecraft.world.level.biome.Biome> ROCKY_SKY_ISLAND =
            biomeKey("rocky_sky_island");

    public static final ResourceKey<net.minecraft.world.level.biome.Biome> CRYSTAL_SKY_ISLAND =
            biomeKey("crystal_sky_island");

    public static final ResourceKey<net.minecraft.world.level.biome.Biome> STORM_SKY_ISLAND =
            biomeKey("storm_sky_island");

    public static final ResourceKey<net.minecraft.world.level.biome.Biome> RUINED_SKY_ISLAND =
            biomeKey("ruined_sky_island");

    // ─── Dimension ────────────────────────────────────────────────────────────

    /** The Astral Sky dimension key — use this anywhere you need Level.dimension() checks. */
    public static final ResourceKey<Level> ASTRAL_SKY_DIMENSION =
            ResourceKey.create(Registries.DIMENSION, rl("astral_sky"));

    /** Dimension type key. */
    public static final ResourceKey<DimensionType> ASTRAL_SKY_DIMENSION_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, rl("astral_sky"));

    // ─── Helpers ──────────────────────────────────────────────────────────────

    public static void init() {
        Constants.LOG.info("Astral Skies worldgen keys loaded. Dimension: {}", ASTRAL_SKY_DIMENSION.location());
    }

    private static ResourceKey<net.minecraft.world.level.biome.Biome> biomeKey(String path) {
        return ResourceKey.create(Registries.BIOME, rl(path));
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
