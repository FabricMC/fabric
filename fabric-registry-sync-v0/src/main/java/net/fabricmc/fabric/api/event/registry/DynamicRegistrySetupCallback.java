package net.fabricmc.fabric.api.event.registry;

import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface DynamicRegistrySetupCallback {
	void onSetupRegistry(DynamicRegistryManager registryManager);

	Event<DynamicRegistrySetupCallback> EVENT = EventFactory.createArrayBacked(
			DynamicRegistrySetupCallback.class,
			callbacks -> registryManager -> {
				for (DynamicRegistrySetupCallback callback : callbacks) {
					callback.onSetupRegistry(registryManager);
				}
			}
	);
}
