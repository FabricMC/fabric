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
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.closeScreen;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.enableDebugHud;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.openGameMenu;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.openInventory;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.setPerspective;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.submitAndWait;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.takeScreenshot;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.waitForLoadingComplete;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.waitForScreen;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.waitForWorldTicks;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.spongepowered.asm.mixin.MixinEnvironment;

import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.option.Perspective;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FabricApiAutoTestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
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
			waitForWorldTicks(200);
			takeScreenshot("in_game_overworld");
		}

		MixinEnvironment.getCurrentEnvironment().audit();

		{
			// See if the player render events are working.
			setPerspective(Perspective.THIRD_PERSON_BACK);
			takeScreenshot("in_game_overworld_third_person");
		}

		{
			openInventory();
			takeScreenshot("in_game_inventory");
			closeScreen();
		}

		{
			openGameMenu();
			takeScreenshot("game_menu");
			clickScreenButton("menu.returnToMenu");
		}

		{
			waitForScreen(TitleScreen.class);
			clickScreenButton("menu.quit");
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
