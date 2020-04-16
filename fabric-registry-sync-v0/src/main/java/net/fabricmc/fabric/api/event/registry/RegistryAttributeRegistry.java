package net.fabricmc.fabric.api.event.registry;

import net.fabricmc.fabric.impl.registry.sync.RegistryAttributeRegistryImpl;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

//TODO better class name here lol
public interface RegistryAttributeRegistry {

	RegistryAttributeRegistry INSTANCE = RegistryAttributeRegistryImpl.INSTANCE;

	void registerAttributes(RegistryAttribute attribute, Registry<?>... registries);

	boolean hasAttribute(Registry<?> registry, RegistryAttribute attribute);

	default boolean hasAttribute(Identifier registryID, RegistryAttribute attribute) {
		return hasAttribute(Registry.REGISTRIES.get(registryID), attribute);
	}

}
