package net.fabricmc.fabric.api.event.registry;

import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FabricRegistryUtils {
	/**
	 * Register an object with a given registry that will not be synced to any clients.
	 * If this registry entry is present on the client it will be given a new id that will not match the server!
	 *
	 * @param registry The target registry
	 * @param id the {@link Identifier} to be used to register the entry
	 * @param entry the registry entry to be added to the target registry
	 * @param <T> The type of the registry/entry
	 * @return Returns the input entry
	 */
	public static <T> T registerServerOnlyRegistryEntry(Registry<T> registry, Identifier id, T entry) {
		if (!RegistryAttributeHolder.get(registry).hasAttribute(RegistryAttribute.SYNCED)) {
			throw new UnsupportedOperationException("Cannot register a server only entry to a none synced registry.");
		}

		T result = Registry.register(registry, id, entry);
		RegistrySyncManager.addServerOnlyRegistryId(registry, id);
		return result;
	}
}
