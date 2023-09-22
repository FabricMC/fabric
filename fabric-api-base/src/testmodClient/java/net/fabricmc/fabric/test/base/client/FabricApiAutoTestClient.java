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

package net.fabricmc.fabric.test.base.client;

import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.clickScreenButton;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.disableDebugHud;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.enableDebugHud;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.openGameMenu;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.submitAndWait;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.takeScreenshot;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.waitForLoadingComplete;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.waitForPendingChunks;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.waitForScreen;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

public class FabricApiAutoTestClient implements ClientModInitializer {
	private static final String ENTRYPOINT_KEY = "fabric-clienttest";

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> {
			dispatcher.register(ClientCommandManager.literal("client_test").executes(context -> {
				var thread = new Thread(() -> {
					try {
						final List<FabricClientTest> tests = FabricLoader.getInstance()
								.getEntrypoints(ENTRYPOINT_KEY, FabricClientTest.class);
						executeTests(tests, FabricClientTest.Context.WORLD);

						context.getSource().sendFeedback(Text.literal("Complete"));
					} catch (Throwable t) {
						t.printStackTrace();
						context.getSource().sendError(Text.literal(t.getMessage()));
					}
				});
				thread.setName("Fabric Auto Test");
				thread.start();
				return 0;
			}));
		});

		if (System.getProperty("fabric.autoTest") == null) {
			return;
		}

		var thread = new Thread(() -> {
			try {
				runTest();
			} catch (Throwable t) {
				t.printStackTrace();
				System.exit(1);
			}
		});
		thread.setName("Fabric Auto Test");
		thread.start();
	}

	private void runTest() {
		waitForLoadingComplete();

		final boolean onboardAccessibility = submitAndWait(client -> client.options.onboardAccessibility);

		if (onboardAccessibility) {
			waitForScreen(AccessibilityOnboardingScreen.class);
			takeScreenshot("onboarding_screen");
			clickScreenButton("gui.continue");
		}

		{
			waitForScreen(TitleScreen.class);
			takeScreenshot("title_screen");
			clickScreenButton("menu.singleplayer");
		}

		final List<FabricClientTest> tests = FabricLoader.getInstance()
				.getEntrypoints(ENTRYPOINT_KEY, FabricClientTest.class);

		executeTests(tests, FabricClientTest.Context.GAME);

		loadWorld();
		executeTests(tests, FabricClientTest.Context.WORLD);
		quitWorld();

		final String serverJar = System.getProperty("fabric.test.serverJar");

		if (serverJar != null) {
			var serverRunner = new ServerRunner(Paths.get(serverJar));
            CompletableFuture<Void> server = serverRunner.run();
			joinServer();
			executeTests(tests, FabricClientTest.Context.SERVER);
			var result = server.join();
		}

		quitGame();
	}

	private void loadWorld() {
		if (!isDirEmpty(FabricLoader.getInstance().getGameDir().resolve("saves"))) {
			waitForScreen(SelectWorldScreen.class);
			takeScreenshot("select_world_screen");
			clickScreenButton("selectWorld.create");
		}

		{
			waitForScreen(CreateWorldScreen.class);
			clickScreenButton("selectWorld.gameMode");
			clickScreenButton("selectWorld.gameMode");
			takeScreenshot("create_world_screen");
			clickScreenButton("selectWorld.create");
		}

		{
			// API test mods use experimental features
			waitForScreen(ConfirmScreen.class);
			clickScreenButton("gui.yes");
		}

		{
			enableDebugHud();
			waitForPendingChunks();
			takeScreenshot("in_game_overworld");
			disableDebugHud();
		}
	}

	private void quitWorld() {
		openGameMenu();
		takeScreenshot("game_menu");
		clickScreenButton("menu.returnToMenu");
	}

	private void joinServer() {

	}

	private void quitGame() {
		{
			waitForScreen(TitleScreen.class);
			clickScreenButton("menu.quit");
		}
	}

	private void executeTests(List<FabricClientTest> tests, FabricClientTest.Context context) {
		for (FabricClientTest test : tests) {
			if (!test.getContext().equals(context)) {
				continue;
			}

			for (Method method : test.getClass().getMethods()) {
				if (!method.isAnnotationPresent(ClientTest.class)) {
					continue;
				}

				try {
					method.invoke(test);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException("Failed to invoke test method", e);
				}
			}
		}
	}

	private boolean isDirEmpty(Path path) {
		try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
			return !directory.iterator().hasNext();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
