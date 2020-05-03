package net.fabricmc.fabric.impl.registry.sync;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;

public class RegistryAttributesImpl<T> implements RegistryAttributeHolder<T> {
	private static final Map<Registry<?>, RegistryAttributeHolder<?>> registryAttributeMap = new HashMap<>();

	public static <T> RegistryAttributeHolder<T> get(Registry<T> registry) {
		//noinspection unchecked
		return (RegistryAttributeHolder<T>) registryAttributeMap.computeIfAbsent(registry, r -> new RegistryAttributesImpl<>());
	}

	private final Set<RegistryAttribute> attributes = new HashSet<>();

	@Override
	public RegistryAttributeHolder<T> addAttribute(RegistryAttribute attribute) {
		attributes.add(attribute);
		return this;
	}

	@Override
	public boolean hasAttribute(RegistryAttribute attribute) {
		return attributes.contains(attribute);
	}
}
