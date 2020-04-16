package net.fabricmc.fabric.impl.registry.sync;

import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeRegistry;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class RegistryAttributeRegistryImpl implements RegistryAttributeRegistry {

	public static final RegistryAttributeRegistryImpl INSTANCE = new RegistryAttributeRegistryImpl();

	private final Map<Registry<?>, Set<RegistryAttribute>> registryAttributeMap = new HashMap<>();

	private RegistryAttributeRegistryImpl() {
	}

	@Override
	public void registerAttributes(RegistryAttribute attribute, Registry<?>... registries) {
		for (Registry<?> registry : registries) {
			registryAttributeMap.computeIfAbsent(registry, r -> new HashSet<>()).add(attribute);
		}
	}

	@Override
	public boolean hasAttribute(Registry<?> registry, RegistryAttribute attribute) {
		return getAttributes(registry).contains(attribute);
	}

	private Set<RegistryAttribute> getAttributes(Registry<?> registry) {
		return registryAttributeMap.getOrDefault(registry, Collections.emptySet());
	}
}
