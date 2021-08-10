package net.fabricmc.fabric.impl.client.gametest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.test.TestFunctions;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

@ApiStatus.Internal
public final class FabricGameTestModInitializer implements ModInitializer {
	private static final String ENTRYPOINT_KEY = "fabric-game-test";
	private static final Map<Class<?>, String> GAME_TEST_IDS = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		List<EntrypointContainer<Object>> entrypointContainers = FabricLoader.getInstance()
				.getEntrypointContainers(ENTRYPOINT_KEY, Object.class);

		for (EntrypointContainer<Object> container : entrypointContainers) {
			Class<?> testClass = container.getEntrypoint().getClass();
			String modid = container.getProvider().getMetadata().getId();

			if (GAME_TEST_IDS.containsKey(testClass)) {
				throw new UnsupportedOperationException("Test class (%s) has already been registered with mod (%s)".formatted(testClass.getCanonicalName(), modid));
			}

			GAME_TEST_IDS.put(testClass, modid);
			TestFunctions.register(testClass);

			LOGGER.debug("Registered test class {} for mod {}", testClass.getCanonicalName(), modid);
		}
	}

	public static String getModIdForTestClass(Class<?> testClass) {
		if (!GAME_TEST_IDS.containsKey(testClass)) {
			throw new UnsupportedOperationException("The test class (%s) was not registered using the '%s' entrypoint".formatted(testClass.getCanonicalName(), ENTRYPOINT_KEY));
		}

		return GAME_TEST_IDS.get(testClass);
	}
}
