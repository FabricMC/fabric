package net.fabricmc.fabric.api.fluids.v1.minecraft;


import java.util.Objects;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

/**
 * the ids for all vanilla fluids.
 */
public class FluidUtil {
	public static boolean miscible(Fluid a, Fluid b) {
		return Fluids.EMPTY.equals(a) || Fluids.EMPTY.equals(b) || Objects.equals(a, b);
	}

	public static Fluid tryFindNonEmpty(Fluid a, Fluid b) {
		return Fluids.EMPTY.equals(a) ? b : a;
	}
}
