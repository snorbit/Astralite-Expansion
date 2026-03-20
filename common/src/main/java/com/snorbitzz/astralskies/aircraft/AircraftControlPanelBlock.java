package com.snorbitzz.astralskies.aircraft;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Aircraft Control Panel block.
 *
 * Interaction behaviour:
 *   - Shift + right-click  →  trigger AircraftAssembler to scan & assemble/disassemble
 *   - Right-click (not shift) → mount the aircraft entity if one is assembled at this pos
 *
 * The actual multi-block scan and entity spawning is delegated to AircraftAssembler
 * to keep this class clean.
 */
public class AircraftControlPanelBlock extends Block {

    public AircraftControlPanelBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level,
                                               BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (player.isShiftKeyDown()) {
            // Attempt to assemble or disassemble the aircraft
            boolean assembled = AircraftAssembler.tryAssemble(level, pos, player);
            if (!assembled) {
                AircraftAssembler.tryDisassemble(level, pos, player);
            }
        } else {
            // Attempt to mount an already-assembled aircraft
            AircraftAssembler.tryMount(level, pos, player);
        }

        return InteractionResult.CONSUME;
    }
}
