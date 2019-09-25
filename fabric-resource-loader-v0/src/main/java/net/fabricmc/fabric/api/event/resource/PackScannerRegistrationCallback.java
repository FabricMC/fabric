package net.fabricmc.fabric.api.event.resource;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;

/**
 * Callback for registering custom resource pack scanners.
 *
 * @param <T> the resource pack container type
 */
public interface PackScannerRegistrationCallback<T extends ResourcePackContainer> {

	/**
	 * The event for registering custom scanners for client resource packs.
	 */
	Event<PackScannerRegistrationCallback<ClientResourcePackContainer>> RESOURCE = EventFactory.createArrayBacked(PackScannerRegistrationCallback.class,
		listeners -> manager -> {
			for (PackScannerRegistrationCallback<ClientResourcePackContainer> each : listeners) {
				each.registerTo(manager);
			}
		}
	);

	/**
	 * The event for registering custom scanners for data packs.
	 */
	Event<PackScannerRegistrationCallback<ResourcePackContainer>> DATA = EventFactory.createArrayBacked(PackScannerRegistrationCallback.class,
		listeners -> manager -> {
			for (PackScannerRegistrationCallback<ResourcePackContainer> each : listeners) {
				each.registerTo(manager);
			}
		}
	);

	/**
	 * Register the creators to the pack container manager.
	 *
	 * @param manager the manager
	 */
	void registerTo(ResourcePackContainerManager<T> manager);
}
