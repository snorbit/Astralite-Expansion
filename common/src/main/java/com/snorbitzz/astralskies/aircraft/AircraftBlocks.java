package com.snorbitzz.astralskies.aircraft;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

/**
 * Aircraft component blocks.
 *
 * An airship is assembled by placing these blocks in proximity:
 *   - At least 4 Hull Blocks     (structural body)
 *   - At least 1 Engine Block    (powered by Astralite Scrap as fuel)
 *   - At least 1 Propeller Block (generates lift)
 *   - Exactly 1 Control Panel    (the seat + activation point)
 *
 * Right-clicking the Control Panel while crouching launches the aircraft.
 * Right-clicking it while mounted opens a simple throttle/pitch UI (Phase 5).
 *
 * The multi-block assembly scanner is in AircraftAssembler.java.
 */
public class AircraftBlocks {

    // ─── Structural ───────────────────────────────────────────────────────────

    /**
     * Aircraft Hull Block — the main body material.
     * Lightweight teal-cyan metal plate; requires pickaxe.
     * Craft: 3 Astralite Scrap in a row → 3 hull blocks.
     */
    public static final Block AIRCRAFT_HULL = register("aircraft_hull",
            new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(3.0f, 8.0f)
                    .sound(SoundType.METAL)));

    // ─── Engine ───────────────────────────────────────────────────────────────

    /**
     * Aircraft Engine Block — consumes Astralite Scrap as fuel when active.
     * Has an ACTIVE boolean blockstate that switches on when the aircraft is powered.
     * Emits light level 7 when active.
     */
    public static final Block AIRCRAFT_ENGINE = register("aircraft_engine",
            new AircraftEngineBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(4.0f, 10.0f)
                    .sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(AircraftEngineBlock.ACTIVE) ? 7 : 0)));

    // ─── Propeller ────────────────────────────────────────────────────────────

    /**
     * Propeller Block — generates vertical lift when the engine is running.
     * Spins visually (handled via blockstate + model rotation in a future phase).
     * Has an ACTIVE state that mirrors the engine.
     */
    public static final Block AIRCRAFT_PROPELLER = register("aircraft_propeller",
            new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    // ─── Control Panel ────────────────────────────────────────────────────────

    /**
     * Control Panel — the pilot's seat.
     * Right-click: mount/dismount the aircraft.
     * Shift + right-click: assemble/disassemble the multi-block aircraft.
     * Only one per aircraft. Emits a faint glow (light 3).
     */
    public static final Block AIRCRAFT_CONTROL_PANEL = register("aircraft_control_panel",
            new AircraftControlPanelBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(3.5f, 8.0f)
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 3)
                    .noOcclusion()));

    // ─── Init & Registration Helpers ─────────────────────────────────────────

    public static void init() {
        Constants.LOG.info("Registering Aircraft Blocks");
    }

    private static Block register(String name, Block block) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        Registry.register(BuiltInRegistries.ITEM, id,
                new BlockItem(block, new Item.Properties()));
        return block;
    }
}
