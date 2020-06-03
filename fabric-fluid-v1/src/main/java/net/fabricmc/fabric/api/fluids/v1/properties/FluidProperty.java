package net.fabricmc.fabric.api.fluids.v1.properties;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.Tag;

/**
 * a property of a fluid.
 *
 * @param <T> the type of data this property handles
 */
public interface FluidProperty<T extends Tag> {
	FluidProperty<?> DEFAULT = new FluidProperty<Tag>() {
		@Override
		public boolean areCompatible(Fluid fluid, Tag aData, long aAmount, Tag bData, long bAmount) {
			return true;
		}

		@Override
		public Tag merge(Fluid fluid, Tag aData, long aAmount, Tag bData, long bAmount) {
			return aData.copy();
		}
	};

	/**
	 * checks if 2 fluid volume's data are compatible with one another.
	 *
	 * @param fluid the fluid being merged
	 * @param aData the data in the first volume
	 * @param aAmount the amount of the first volume
	 * @param bData the data in the second volume
	 * @param bAmount the amount of the second volume
	 * @return true if the 2 data values are compatible with one another
	 */
	boolean areCompatible(Fluid fluid, T aData, long aAmount, T bData, long bAmount);

	/**
	 * combine the two data values together.
	 *
	 * @return a newly combined and merged data of the two fluids
	 */
	T merge(Fluid fluid, T aData, long aAmount, T bData, long bAmount);


}
