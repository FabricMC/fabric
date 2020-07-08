package net.fabricmc.fabric.api.client.item.v1;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FabricItemUpdateAnimationHandlers {
	private static final Map<Item, UpdateAnimationHandler> handlers = new HashMap<>();
	private static final UpdateAnimationHandler DEFAULT = (original, updated) -> true;

	public static void register(Item item, UpdateAnimationHandler handler) {
		if (handlers.containsKey(item)) {
			Identifier registryID = Registry.ITEM.getId(item);
			throw new UnsupportedOperationException(String.format("Attempted to register an Item Update Animation Handler for %s, but one was already registered!", registryID.toString()));
		} else {
			handlers.put(item, handler);
		}
	}

	public static UpdateAnimationHandler get(Item item) {
		return handlers.getOrDefault(item, DEFAULT);
	}
}
