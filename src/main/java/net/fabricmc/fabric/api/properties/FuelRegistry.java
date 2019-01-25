package net.fabricmc.fabric.api.properties;

import net.fabricmc.fabric.api.util.Item2ObjectMap;
import net.fabricmc.fabric.impl.item.FuelRegistryImpl;

/**
 * Registry of items to 0-32767 fuel burn time values, in in-game ticks.
 */
public interface FuelRegistry extends Item2ObjectMap<Integer> {
	public static final FuelRegistry INSTANCE = new FuelRegistryImpl();
}
