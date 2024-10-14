package net.fabricmc.fabric.test.registry.sync;

import com.mojang.logging.LogUtils;

import com.mojang.serialization.Codec;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegistryOrderTest implements ModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static final RegistryKey<Registry<String>> TEST_ORDERED_REGISTRY_KEY =
			RegistryKey.ofRegistry(Identifier.of("fabric", "test_ordered"));
	private static final RegistryKey<String> ORDERED_ENTRY_KEY =
			RegistryKey.of(TEST_ORDERED_REGISTRY_KEY, Identifier.of("fabric-registry-sync-v0-testmod", "test"));

	@Override
	public void onInitialize() {
		DynamicRegistries.register(TEST_ORDERED_REGISTRY_KEY, Codec.STRING);
		AtomicBoolean valueReceived = new AtomicBoolean();

		DynamicRegistrySetupCallback.EVENT.register(registryView -> {
			registryView.getOptional(TEST_ORDERED_REGISTRY_KEY).ifPresent(testRegistry -> {
				// While we do not support registry overwriting, we are registering many values
				// to the same key here in order to greatly increase the odds of failure.
				for (int i = 0; i < 1000; i++) {
					Registry.register(testRegistry, ORDERED_ENTRY_KEY, "Incorrect value: " + i);
				}

				LOGGER.info("Added dummy values to {} for key: {}", TEST_ORDERED_REGISTRY_KEY, ORDERED_ENTRY_KEY);
			});
			registryView.registerEntryAdded(TEST_ORDERED_REGISTRY_KEY, (rawId, id, value) -> {
				valueReceived.set(true);

				LOGGER.info("Value received from data pack for key: {} = {}", id, value);
			});
		});

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			Validate.isTrue(valueReceived.get(), "Value not present in data pack for key: %s", ORDERED_ENTRY_KEY);

			Registry<String> testRegistry = server.getRegistryManager().get(TEST_ORDERED_REGISTRY_KEY);
			String actualValue = testRegistry.get(ORDERED_ENTRY_KEY);

			// We expect that a data pack value will always supersede our 1000 dummy values.
			Validate.isTrue(Objects.equals(actualValue, "Value from data pack"), actualValue);

			LOGGER.info("Confirmed write order for test registry: {}", TEST_ORDERED_REGISTRY_KEY);
		});
	}
}
