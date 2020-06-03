package net.fabricmc.fabric.api.fluids.v1.minecraft.properties;

import net.minecraft.nbt.IntTag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds;
import net.fabricmc.fabric.api.fluids.v1.properties.FluidProperty;

public class CustomPotionColorProperty implements FluidProperty<IntTag> {
	@Override
	public boolean areCompatible(Identifier fluid, IntTag aData, long aAmount, IntTag bData, long bAmount) {
		return FluidIds.POTION.equals(fluid);
	}

	@Override
	public IntTag merge(Identifier fluid, IntTag aData, long aAmount, IntTag bData, long bAmount) {
		return IntTag.of(average(aData.getInt(), bData.getInt()));
	}

	// "good enough" color blending algorithm
	private static int average(int rgbLeft, int rgbRight) {
		return rgbLeft & 0xFF000000 | avg(rgbLeft, rgbRight, 16) | avg(rgbLeft, rgbRight, 8) | avg(rgbLeft, rgbRight, 0);
	}

	private static int avg(int a, int b, int off) {
		return ((a >> off & 0xFF) + (b >> off & 0xFF)) / 2 << off;
	}
}
