package net.fabricmc.fabric.impl.registry.sync;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;

public final class RegistryAttributeImpl implements RegistryAttributeHolder {
	private static final Map<RegistryKey<?>, RegistryAttributeHolder> HOLDER_MAP = new HashMap<>();

	public static RegistryAttributeHolder getHolder(RegistryKey<?> registryKey) {
		return HOLDER_MAP.computeIfAbsent(registryKey, key -> new RegistryAttributeImpl());
	}

	private final EnumSet<RegistryAttribute> attributes = EnumSet.noneOf(RegistryAttribute.class);

	private RegistryAttributeImpl() {
	}

	@Override
	public RegistryAttributeHolder addAttribute(RegistryAttribute attribute) {
		attributes.add(attribute);
		return this;
	}

	@Override
	public boolean hasAttribute(RegistryAttribute attribute) {
		return attributes.contains(attribute);
	}
}
