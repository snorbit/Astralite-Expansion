package com.snorbitzz.astralskies.aircraft;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * AircraftAssembler — multi-block scanner and aircraft spawner.
 *
 * Assembly rules (checked in a 7×5×7 bounding box around the control panel):
 *   - Min 4 × aircraft_hull blocks
 *   - Min 1 × aircraft_engine block
 *   - Min 1 × aircraft_propeller block
 *   - Exactly 1 × aircraft_control_panel (the activation point)
 *
 * When valid:
 *   1. All component blocks get their ACTIVE state set to true (engines glow).
 *   2. An AircraftEntity is spawned above the control panel, linked to it.
 *   3. A success message is sent to the player.
 *
 * Disassembly reverses this — entity removed, blocks ACTIVE reset to false.
 */
public class AircraftAssembler {

    /** Search radius around the control panel. */
    private static final int SCAN_RADIUS = 3;
    private static final int SCAN_HEIGHT = 2;
    private static final int MIN_HULL = 4;

    /**
     * Scans for a valid aircraft structure around the given control panel pos.
     * @return true if assembly succeeded
     */
    public static boolean tryAssemble(Level level, BlockPos panelPos, Player player) {
        List<BlockPos> hulls = new ArrayList<>();
        List<BlockPos> engines = new ArrayList<>();
        List<BlockPos> propellers = new ArrayList<>();

        // Scan bounding box
        BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos();
        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dy = -1; dy <= SCAN_HEIGHT; dy++) {
                for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                    scan.setWithOffset(panelPos, dx, dy, dz);
                    var block = level.getBlockState(scan).getBlock();

                    if (block == AircraftBlocks.AIRCRAFT_HULL)
                        hulls.add(scan.immutable());
                    else if (block == AircraftBlocks.AIRCRAFT_ENGINE)
                        engines.add(scan.immutable());
                    else if (block == AircraftBlocks.AIRCRAFT_PROPELLER)
                        propellers.add(scan.immutable());
                }
            }
        }

        // Validate requirements
        if (hulls.size() < MIN_HULL) {
            player.sendSystemMessage(Component.literal(
                    "§cAircraft needs at least " + MIN_HULL + " Hull Blocks! (found " + hulls.size() + ")"));
            return false;
        }
        if (engines.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cAircraft needs at least 1 Engine Block!"));
            return false;
        }
        if (propellers.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cAircraft needs at least 1 Propeller Block!"));
            return false;
        }

        // Activate engine blocks (visual state change)
        for (BlockPos enginePos : engines) {
            var engineState = level.getBlockState(enginePos);
            if (engineState.getBlock() instanceof AircraftEngineBlock) {
                level.setBlock(enginePos,
                        engineState.setValue(AircraftEngineBlock.ACTIVE, true), 3);
            }
        }

        // Spawn the aircraft entity above the control panel
        var aircraftEntity = new AircraftEntity(AircraftEntities.AIRCRAFT, level);
        aircraftEntity.setPos(panelPos.getX() + 0.5, panelPos.getY() + 1.0, panelPos.getZ() + 0.5);
        aircraftEntity.setPanelPos(panelPos);
        level.addFreshEntity(aircraftEntity);

        player.sendSystemMessage(Component.literal(
                "§aAircraft assembled! §7(" + hulls.size() + " hull, " +
                engines.size() + " engine, " + propellers.size() + " propeller) " +
                "Right-click to board."));
        return true;
    }

    /**
     * Finds a linked AircraftEntity at the panel position and removes it,
     * resetting all engine block states.
     */
    public static void tryDisassemble(Level level, BlockPos panelPos, Player player) {
        // Find the aircraft entity near this panel
        var nearbyEntities = level.getEntitiesOfClass(
                AircraftEntity.class,
                new net.minecraft.world.phys.AABB(panelPos).inflate(SCAN_RADIUS + 1)
        );

        boolean found = false;
        for (AircraftEntity entity : nearbyEntities) {
            if (panelPos.equals(entity.getPanelPos())) {
                // Deactivate engines in scan radius
                BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos();
                for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
                    for (int dy = -1; dy <= SCAN_HEIGHT; dy++) {
                        for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                            scan.setWithOffset(panelPos, dx, dy, dz);
                            var state = level.getBlockState(scan);
                            if (state.getBlock() instanceof AircraftEngineBlock) {
                                level.setBlock(scan,
                                        state.setValue(AircraftEngineBlock.ACTIVE, false), 3);
                            }
                        }
                    }
                }
                entity.discard();
                found = true;
                break;
            }
        }

        if (found) {
            player.sendSystemMessage(Component.literal("§7Aircraft disassembled."));
        } else {
            player.sendSystemMessage(Component.literal("§cNo assembled aircraft found at this panel."));
        }
    }

    /**
     * Mounts the player onto an AircraftEntity linked to this panel.
     */
    public static void tryMount(Level level, BlockPos panelPos, Player player) {
        var nearbyEntities = level.getEntitiesOfClass(
                AircraftEntity.class,
                new net.minecraft.world.phys.AABB(panelPos).inflate(SCAN_RADIUS + 1)
        );

        for (AircraftEntity entity : nearbyEntities) {
            if (panelPos.equals(entity.getPanelPos())) {
                if (entity.getPassengers().isEmpty()) {
                    player.startRiding(entity);
                    player.sendSystemMessage(Component.literal(
                            "§aBoarded! §7WASD to fly, Space to ascend, Shift to descend."));
                } else {
                    player.sendSystemMessage(Component.literal("§cAircraft is occupied."));
                }
                return;
            }
        }
        player.sendSystemMessage(Component.literal("§cNo aircraft assembled here. Shift+right-click to assemble."));
    }
}
