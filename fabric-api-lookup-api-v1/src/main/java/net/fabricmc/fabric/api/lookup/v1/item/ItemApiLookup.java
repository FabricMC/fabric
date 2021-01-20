package net.fabricmc.fabric.api.lookup.v1.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemConvertible;

public interface ItemApiLookup<T, C> {
	@Nullable
	T get(ItemKey stack, C context);

	void register(ItemApiProvider<T, C> provider, ItemConvertible... items);

	void registerFallback(ItemApiProvider<T, C> provider);

	@FunctionalInterface
	interface ItemApiProvider<T, C> {
		@Nullable
		T get(ItemKey itemKey, C context);
	}
}
