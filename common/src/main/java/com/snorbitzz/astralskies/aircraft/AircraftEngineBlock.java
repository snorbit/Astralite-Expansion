package com.snorbitzz.astralskies.aircraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Engine block — has an ACTIVE blockstate to toggle between idle and powered visuals.
 * The AircraftAssembler flips this to true when a valid aircraft is assembled.
 */
public class AircraftEngineBlock extends Block {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public AircraftEngineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
}
