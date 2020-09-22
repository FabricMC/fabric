package net.fabricmc.fabric.impl.object.builder;

import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;

public interface AbstractBlockInternals {
	void setPistonBehavior(PistonBehavior pistonBehavior);
	boolean isReplaceable(BlockState state);
	void setReplaceable(boolean replaceable);
	boolean isSolid(BlockState state);
	void setSolid(boolean solid);
}
