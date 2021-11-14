package net.fabricmc.fabric.impl.fluid;

import org.jetbrains.annotations.NotNull;

import net.minecraft.fluid.FluidState;

/**
 * Implements some fluid-related client-side player entity features.
 */
public interface FabricFluidClientPlayerEntity {
	/**
	 * Executed when a player enters a fluid.
	 * @param fluidState Fluid in which the player is entered.
	 */
	void enterInFluid(@NotNull FluidState fluidState);

	/**
	 * Executed when a player exit from a fluid.
	 * @param fluidState Fluid from which the player is exited.
	 */
	void exitFromFluid(@NotNull FluidState fluidState);

	/**
	 * Executed when a player exit from a fluid and enters another.
	 * @param oldFluidState Fluid from which the player is exited.
	 * @param newFluidState Fluid in which the player is entered.
	 */
	void changedFluid(@NotNull FluidState oldFluidState, @NotNull FluidState newFluidState);
}
