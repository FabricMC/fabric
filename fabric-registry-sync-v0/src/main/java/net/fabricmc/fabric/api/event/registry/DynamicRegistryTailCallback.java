package net.fabricmc.fabric.api.event.registry;

import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event gets triggered when a new {@link DynamicRegistryManager} gets created, but after it gets filled.
 * This is in contract to {@link DynamicRegistrySetupCallback} which runs before it is filled.
 * Therefore, this is the ideal place to touch multiple dynamic registries at once, for example.
 * Below is an example usage of the event.
 *
 * <pre>
 *  * {@code
 *  * DynamicRegistryTailCallback.EVENT.register(registryManager -> {
 *  *     Registry<StructurePool> pools = registryManager.get(Registry.STRUCTURE_POOL_KEY);
 *  *     Registry<StructurePoolProcessorList> lists = registryManager.get(Registry.STRUCTURE_PROCESSOR_LIST_KEY);
 *  *     ...
 *  * });
 *  * }
 *  * </pre>
 */
@FunctionalInterface
public interface DynamicRegistryTailCallback {
	void onRegistrationFinish(DynamicRegistryManager registryManager);

	Event<DynamicRegistryTailCallback> EVENT = EventFactory.createArrayBacked(
			DynamicRegistryTailCallback.class,
			callbacks -> registryManager -> {
				for (DynamicRegistryTailCallback callback : callbacks) {
					callback.onRegistrationFinish(registryManager);
				}
			}
	);
}
