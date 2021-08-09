/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.client.gametest;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import net.minecraft.test.TestServer;
import net.minecraft.test.TestUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.level.storage.LevelStorage;

import net.fabricmc.loader.FabricLoader;

public class FabricGameTestHelperImpl {
	public static final boolean ENABLED = Boolean.getBoolean("fabric-api.gametest.server");

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Class<?>, String> GAME_TEST_IDS = new HashMap<>();

	private FabricGameTestHelperImpl() {
	}

	public static void runHeadlessServer(LevelStorage.Session session, ResourcePackManager resourcePackManager, ServerResourceManager serverResourceManager, DynamicRegistryManager.Impl registryManager) {
		LOGGER.info("Starting test server");
		MinecraftServer server = TestServer.startServer(thread -> {
			TestServer testServer = new TestServer(thread, session, resourcePackManager, serverResourceManager, getBatches(), BlockPos.ORIGIN, registryManager);
			FabricLoader.INSTANCE.setGameInstance(testServer);
			return testServer;
		});
	}

	public static void register(Class<?> testClass, String modid) {
		GAME_TEST_IDS.put(testClass, modid);
		TestFunctions.register(testClass);
	}

	public static String getModIdForTestClass(Class<?> testClass) {
		if (!GAME_TEST_IDS.containsKey(testClass)) {
			throw new UnsupportedOperationException("Use FabricGameTestRegistry.register to register your test class");
		}

		return GAME_TEST_IDS.get(testClass);
	}

	// Moved out to here as I expect we will want a FabricTestContext, or a way for a mod to provide their own context
	// We can also have better error handling.
	public static Consumer<TestContext> invokeTestMethod(Method method) {
		return testContext -> {
			try {
				Object object = method.getDeclaringClass().getConstructor().newInstance();
				method.invoke(object, testContext);
			} catch (Exception e) {
				// TODO we can have much better error handling
				throw new RuntimeException(e);
			}
		};
	}

	private static Collection<GameTestBatch> getBatches() {
		return TestUtil.createBatches(getTestFunctions());
	}

	private static Collection<TestFunction> getTestFunctions() {
		return TestFunctions.getTestFunctions();
	}
}
