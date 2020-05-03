package net.fabricmc.fabric.api.event.registry;

import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.registry.sync.RegistryAttributesImpl;

public interface RegistryAttributeHolder<T> {
	static <T> RegistryAttributeHolder<T> get(Registry<T> registry) {
		return RegistryAttributesImpl.get(registry);
	}

	RegistryAttributeHolder<T> addAttribute(RegistryAttribute attribute);

	boolean hasAttribute(RegistryAttribute attribute);
}
