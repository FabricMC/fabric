package net.fabricmc.fabric.api.lookup.v1.item;

import java.util.Objects;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.lookup.item.ItemApiLookupRegistryImpl;

public class ItemApiLookupRegistry {
	public static <T, C> ItemApiLookup<T, C> getLookup(Identifier lookupId, Class<T> apiClass, Class<C> contextClass) {
		Objects.requireNonNull(apiClass, "Id of API cannot be null");
		Objects.requireNonNull(contextClass, "Context key cannot be null");

		return ItemApiLookupRegistryImpl.getLookup(lookupId, apiClass, contextClass);
	}

	private ItemApiLookupRegistry() {
	}
}
