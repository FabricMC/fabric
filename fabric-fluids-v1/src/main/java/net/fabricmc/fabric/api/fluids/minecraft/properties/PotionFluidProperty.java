package net.fabricmc.fabric.api.fluids.minecraft.properties;

import net.minecraft.nbt.StringTag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.minecraft.FluidIds;
import net.fabricmc.fabric.api.fluids.properties.FluidProperty;

public class PotionFluidProperty implements FluidProperty<StringTag> {
	@Override
	public boolean areCompatible(Identifier fluid, StringTag aData, long aAmount, StringTag bData, long bAmount) {
		return FluidIds.POTION.equals(fluid) && aData.equals(bData);
	}

	@Override
	public StringTag merge(Identifier fluid, StringTag aData, long aAmount, StringTag bData, long bAmount) {
		return aData.copy();
	}
}
