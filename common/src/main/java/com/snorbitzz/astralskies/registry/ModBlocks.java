package com.snorbitzz.astralskies.registry;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.util.valueproviders.UniformInt;

import java.util.function.Supplier;

/**
 * Central registry for all Astral Skies blocks.
 * All blocks also have a corresponding BlockItem registered automatically.
 */
public class ModBlocks {

    // ─── Sky Island Terrain ───────────────────────────────────────────────────

    /**
     * Floating Stone — the primary terrain block of sky islands.
     * Pale grey-blue, slightly lighter than stone, found across all sky biomes.
     */
    public static final RegistryObject<Block> FLOATING_STONE = register("floating_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.5f, 6.0f)
                    .sound(SoundType.STONE)));

    /**
     * Astral Dirt — sky island surface ground. Dark, starfield-dusted dirt variant.
     */
    public static final RegistryObject<Block> ASTRAL_DIRT = register("astral_dirt",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.6f)
                    .sound(SoundType.GRAVEL)));

    /**
     * Crystal Block — decorative crystal block found on crystal-type sky islands.
     * Translucent purple, no tool requirement for breaking.
     */
    public static final RegistryObject<Block> CRYSTAL_BLOCK = register("crystal_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(1.5f, 3.0f)
                    .sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops()));

    // ─── Astralite Ores ───────────────────────────────────────────────────────

    /**
     * Astralite Ore — the core sky-only ore. ONLY spawns inside sky island stone cores.
     * Drops 1–2 Astralite Scrap + XP. Requires diamond-level tool or higher.
     * Visually: dark sky-stone with glowing blue-green ore veins.
     */
    public static final RegistryObject<Block> ASTRALITE_ORE = register("astralite_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 7),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .requiresCorrectToolForDrops()
                            .strength(5.0f, 9.0f)
                            .sound(SoundType.STONE)));

    /**
     * Deepslate Astralite Ore — found in deeper, denser sky island cores.
     * Slightly tougher than the regular variant.
     */
    public static final RegistryObject<Block> DEEPSLATE_ASTRALITE_ORE = register("deepslate_astralite_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 7),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DEEPSLATE)
                            .requiresCorrectToolForDrops()
                            .strength(6.0f, 9.0f)
                            .sound(SoundType.DEEPSLATE)));

    // ─── Processed Blocks ────────────────────────────────────────────────────

    /**
     * Astralite Block — crafted storage block (9 Astralite Scraps).
     * Glowing solid Astralite. Used as a decorative/structural block and crafting shortcut.
     */
    public static final RegistryObject<Block> ASTRALITE_BLOCK = register("astralite_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(7.0f, 12.0f)
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 4)));

    // ─── Portal ───────────────────────────────────────────────────────────────

    /**
     * Astral Portal Frame — placed in a 3×4 rectangle (like Nether portal).
     * Once ignited by a right-click with an Astral Compass, it opens the
     * Astral Sky dimension portal. Glows gently when formed correctly.
     * Craft: 1 Astralite Block surrounded by 4 Astralite Scrap (+ pattern).
     */
    public static final RegistryObject<Block> ASTRAL_PORTAL_FRAME = register("astral_portal_frame",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(50.0f, 1200.0f)   // Obsidian-level blast resistant
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 3)));   // Faint glow

    // ─── Registration Helpers ─────────────────────────────────────────────────

    public static void init() {
        Constants.LOG.info("Registering Astral Skies Blocks");
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier) {
        T block = supplier.get();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        // Auto-register BlockItem
        Registry.register(BuiltInRegistries.ITEM, id,
                new BlockItem(block, new Item.Properties()));
        return new RegistryObject<>(block);
    }

    /** Minimal holder so callers can use .get() consistently. */
    public static class RegistryObject<T> {
        private final T value;
        public RegistryObject(T value) { this.value = value; }
        public T get() { return value; }
    }
}
