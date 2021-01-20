package net.fabricmc.fabric.impl.lookup.item;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.lookup.v1.ApiLookupMap;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;

public class ItemApiLookupRegistryImpl {
	private static final ApiLookupMap<ItemApiLookup<?, ?>> PROVIDERS = ApiLookupMap.create(ItemApiLookupImpl::new);

	@SuppressWarnings("unchecked")
	public static <T, C> ItemApiLookup<T, C> getLookup(Identifier lookupId, Class<T> apiClass, Class<C> contextClass) {
		return (ItemApiLookup<T, C>) PROVIDERS.getLookup(lookupId, apiClass, contextClass);
	}

	private ItemApiLookupRegistryImpl() {
	}
}
