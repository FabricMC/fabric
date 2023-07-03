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

package net.fabricmc.fabric.test.resource.loader.client;

import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.clickScreenButton;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.openGameMenu;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.submitServer;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.takeScreenshot;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.tapKey;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.waitForClientTicks;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.waitForLoadingComplete;
import static net.fabricmc.fabric.test.base.client.FabricClientTestHelper.waitForScreen;

import org.lwjgl.glfw.GLFW;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;

import net.fabricmc.fabric.test.base.client.ClientTest;
import net.fabricmc.fabric.test.base.client.FabricClientTest;

public class ResourceLoaderClientTest implements FabricClientTest {
	@ClientTest
	public void injectedResources() {
		submitServer((world, player) ->
				world.setBlockState(
						player.getBlockPos().up().offset(player.getHorizontalFacing(), 1),
						Blocks.DIAMOND_BLOCK.getDefaultState()
				)
		);
		// Takes a little bit for our block to appear :D
		waitForClientTicks(5);

		// Default
		takeScreenshot("injected_resources_default");
		// TODO maybe render the block in its own frame buffer and save the png to the git repo?

		// Programmer
		openPackScreen();
		tapKey(GLFW.GLFW_KEY_TAB); // Highlight the available pack list
		tapKey(GLFW.GLFW_KEY_DOWN);
		tapKey(GLFW.GLFW_KEY_DOWN); // Highlight the programmer resources
		tapKey(GLFW.GLFW_KEY_SPACE); // Enable it
		takeScreenshot("resource_pack_screen_programmer");
		closePackScreen();
		takeScreenshot("injected_resources_programmer");
		// TODO maybe render the block in its own frame buffer and save the png to the git repo?

		// High contrast
		openPackScreen();
		tapKey(GLFW.GLFW_KEY_TAB); // Highlight the available pack list
		tapKey(GLFW.GLFW_KEY_DOWN); // Highlight the high contrast pack
		tapKey(GLFW.GLFW_KEY_SPACE); // Enable it
		tapKey(GLFW.GLFW_KEY_DOWN); // Highlight previously enabled programmer pack
		tapKey(GLFW.GLFW_KEY_SPACE); // Disable it
		takeScreenshot("resource_pack_screen_high_contrast");
		closePackScreen();
		takeScreenshot("injected_resources_high_contrast");
		// TODO maybe render the block in its own frame buffer and save the png to the git repo?

		// Disable high contrast pack
		openPackScreen();
		tapKey(GLFW.GLFW_KEY_TAB);
		tapKey(GLFW.GLFW_KEY_TAB); // Highlight the enabled pack list
		tapKey(GLFW.GLFW_KEY_SPACE); // Disable high contrast
		closePackScreen();
	}

	private void openPackScreen() {
		openGameMenu();
		clickScreenButton("menu.options");
		waitForScreen(OptionsScreen.class);
		clickScreenButton("options.resourcepack");
		waitForScreen(PackScreen.class);
	}

	private void closePackScreen() {
		clickScreenButton("gui.done");
		// Wait for the resource pack loading overlay
		waitForLoadingComplete();

		waitForScreen(OptionsScreen.class);
		clickScreenButton("gui.done");
		waitForScreen(GameMenuScreen.class);
		clickScreenButton("menu.returnToGame");
	}
}
