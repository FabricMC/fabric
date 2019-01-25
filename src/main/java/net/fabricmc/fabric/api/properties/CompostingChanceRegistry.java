package net.fabricmc.fabric.api.properties;

import net.fabricmc.fabric.api.util.Item2ObjectMap;
import net.fabricmc.fabric.impl.item.CompostingChanceRegistryImpl;

/**
 * Registry of items to 0.0-1.0 values, defining the chance of a given item
 * increasing the Composter block's level
 */
public interface CompostingChanceRegistry extends Item2ObjectMap<Float> {
	public static final CompostingChanceRegistry INSTANCE = new CompostingChanceRegistryImpl();
}
