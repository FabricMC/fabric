package net.fabricmc.fabric.impl.fluid.extension;

import net.minecraft.fluid.FluidState;

public interface FlowableFluidExtensions {
	public int getMaxLevel();
	
	public void setMaxLevel(int maxLevel);
	
	public boolean isFalling(FluidState state);
}
