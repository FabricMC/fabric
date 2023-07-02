package net.fabricmc.fabric.impl.registry.sync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;

import net.fabricmc.fabric.api.event.registry.DynamicRegistryEvents;

public final class DynamicRegistryRegistrationContextImpl implements DynamicRegistryEvents.RegistrationContext {
	private final List<RegistryLoader.Entry<?>> entries = new ArrayList<>();

	public List<RegistryLoader.Entry<?>> getEntries() {
		return entries;
	}

	@Override
	public <T> void add(RegistryKey<? extends Registry<T>> registryKey, Codec<T> codec) {
		add(new RegistryLoader.Entry<>(registryKey, codec));
	}

	@Override
	public void add(RegistryLoader.Entry<?> entry) {
		entries.add(entry);
	}

	@Override
	public void add(RegistryLoader.Entry<?>... entries) {
		add(Arrays.asList(entries));
	}

	@Override
	public void add(Collection<? extends RegistryLoader.Entry<?>> entries) {
		this.entries.addAll(entries);
	}
}
