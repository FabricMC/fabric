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

package net.fabricmc.fabric.impl.gametest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

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

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.loader.FabricLoader;

@ApiStatus.Internal
public final class FabricGameTestHelper {
	public static final boolean ENABLED = System.getProperty("fabric-api.gametest") != null;

	private static final Logger LOGGER = LogManager.getLogger();

	private FabricGameTestHelper() {
	}

	public static void runHeadlessServer(LevelStorage.Session session, ResourcePackManager resourcePackManager, ServerResourceManager serverResourceManager, DynamicRegistryManager.Impl registryManager) {
		LOGGER.info("Starting test server");
		MinecraftServer server = TestServer.startServer(thread -> {
			TestServer testServer = new TestServer(thread, session, resourcePackManager, serverResourceManager, getBatches(), BlockPos.ORIGIN, registryManager);
			FabricLoader.INSTANCE.setGameInstance(testServer);
			return testServer;
		});
	}

	// Moved out to here as I expect we will want a FabricTestContext, or a way for a mod to provide their own context
	// We can also have better error handling.
	public static Consumer<TestContext> getTestMethodInvoker(Method method) {
		return testContext -> {
			Class<?> testClass = method.getDeclaringClass();

			Constructor<?> constructor;

			try {
				constructor = testClass.getConstructor();
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("Test class (%s) provided by (%s) must have a public default or no args constructor".formatted(testClass.getSimpleName(), FabricGameTestModInitializer.getModIdForTestClass(testClass)));
			}

			Object testObject;

			try {
				testObject = constructor.newInstance();
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Failed to create instance of test class (%s)".formatted(testClass.getCanonicalName()), e);
			}

			if (testClass.isAssignableFrom(FabricGameTest.class)) {
				FabricGameTest fabricGameTest = (FabricGameTest) testObject;
				fabricGameTest.invokeTestMethod(testContext, method);
			} else {
				invokeTestMethod(testContext, method, testObject);
			}
		};
	}

	public static void invokeTestMethod(TestContext testContext, Method method, Object testObject) {
		try {
			method.invoke(testObject, testContext);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Failed to invoke test method (%s) in (%s)".formatted(method.getName(), method.getDeclaringClass().getCanonicalName()), e);
		}
	}

	private static Collection<GameTestBatch> getBatches() {
		return TestUtil.createBatches(getTestFunctions());
	}

	private static Collection<TestFunction> getTestFunctions() {
		return TestFunctions.getTestFunctions();
	}
}
