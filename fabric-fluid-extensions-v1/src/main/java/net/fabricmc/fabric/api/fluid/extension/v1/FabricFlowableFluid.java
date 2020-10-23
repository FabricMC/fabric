package net.fabricmc.fabric.api.fluid.extension.v1;

import net.fabricmc.fabric.impl.fluid.extension.FlowableFluidExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;

/**
 * 
 */
public abstract class FabricFlowableFluid extends FlowableFluid {
	private IntProperty stateIndexProperty;
	
	public FabricFlowableFluid() {
		((FlowableFluidExtensions) this).setMaxLevel(getLevels());
		setDefaultState(getDefaultState().with(stateIndexProperty, 0));
	}

	@Override
	public Fluid getStill() {
		return this;
	}
	
	@Override
	public Fluid getFlowing() {
		return this;
	}

	@Override
	public FluidState getStill(boolean falling) {
		if (falling) {
			return getStill().getDefaultState().with(stateIndexProperty, ((FlowableFluidExtensions) this).getMaxLevel());
		} else {
			return getStill().getDefaultState();
		}
	}

	@Override
	public FluidState getFlowing(int level, boolean falling) {
		return getFlowing().getDefaultState().with(stateIndexProperty, falling ? ((FlowableFluidExtensions) this).getMaxLevel() : level);
	}

	@Override
	public boolean isStill(FluidState state) {
		return state.get(stateIndexProperty) == 0;
	}

	@Override
	public int getLevel(FluidState state) {
		int level = state.get(stateIndexProperty);
		return level == 0 ? ((FlowableFluidExtensions) this).getMaxLevel() : level;
	}
	
	// Overrides duck method in mixin.
	public boolean isFalling(FluidState state) {
		return state.get(stateIndexProperty) == ((FlowableFluidExtensions) this).getMaxLevel();
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
		stateIndexProperty = IntProperty.of("state_index", 0, getLevels());
        builder.add(stateIndexProperty);
	}

	@Override
	protected BlockState toBlockState(FluidState state) {
		return getFluidBlock().getDefaultState().with(stateIndexProperty, state.get(stateIndexProperty));
	}
	
	/**
	 * If a fluid is still, its state index is zero.
	 * If a fluid is flowing, its state index is non-zero and represents its level and height.
	 * 
	 * @return The state index property used in BlockStates and FluidStates which correspond to this fluid.
	 */
	public IntProperty getStateIndexProperty() {
		return stateIndexProperty;
	}
	
	/**
	 * The returned value of this method should not change.
	 * 
	 * @return A FabricFlowableFluidBlock whose getFluid method returns this fluid.
	 */
	public abstract Block getFluidBlock();
	
	/**
	 * The returned value of this method should not change.
	 * 
	 * @return The number of levels this fluid has.
	 */
	public abstract int getLevels();
}
