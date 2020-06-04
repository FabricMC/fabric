package net.fabricmc.fabric.api.fluid.v1.properties;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.Tag;

/**
 * A property of a fluid.
 *
 * @param <T> the type of data this property handles
 */
public interface FluidProperty<T extends Tag> {
	FluidProperty<Tag> DEFAULT = new FluidProperty<Tag>() {
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
	 * Checks if a FluidVolume's data is compatible with another's.
	 *
	 * @param fluid the fluid being merged
	 * @param aData the data in the first volume
	 * @param aAmount the amount of the first volume
	 * @param bData the data in the second volume
	 * @param bAmount the amount of the second volume
	 * @return true if the values are compatible with one another
	 */
	boolean areCompatible(Fluid fluid, T aData, long aAmount, T bData, long bAmount);

	/**
	 * Combine two FluidVolume data values together.
	 *
	 * @return a newly created combined data value from the two given FluidVolumes' values
	 */
	T merge(Fluid fluid, T aData, long aAmount, T bData, long bAmount);
}
