package net.fabricmc.fabric.api.event.registry;

import net.minecraft.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event is fired just before dynamic registries are immutablized,
 * giving modders a last chance to access and change any registry
 * while it's still mutable.
 *
 * @see DynamicRegistryManager
 */
@FunctionalInterface
public interface DynamicRegistryFinalizeCallback {
	void onRegistryFinalize(DynamicRegistryManager registryManager);

	Event<DynamicRegistryFinalizeCallback> EVENT = EventFactory.createArrayBacked(DynamicRegistryFinalizeCallback.class, callbacks -> registryManager -> {
		for (DynamicRegistryFinalizeCallback callback : callbacks) {
			callback.onRegistryFinalize(registryManager);
		}
	});
}
