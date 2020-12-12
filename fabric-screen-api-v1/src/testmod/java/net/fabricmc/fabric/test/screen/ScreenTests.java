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

package net.fabricmc.fabric.test.screen;

import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.FabricScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class ScreenTests implements ClientModInitializer {
	public static final Random RANDOM = new Random();
	private static final Logger LOGGER = LogManager.getLogger("FabricScreenApiTests");
	private static boolean PRINT_RESIZE_SCREEN_EVENTS = System.getProperty("fabric-screen-api-testmod.printResizeScreenEvents") != null;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Started Screen Testmod");
		ScreenEvents.AFTER_INIT.register(this::onInitScreen);
	}

	private void onInitScreen(MinecraftClient client, Screen screen, FabricScreen info, int windowWidth, int windowHeight) {
		LOGGER.info("Initializing {}", screen.getClass().getName());

		if (screen instanceof TitleScreen) {
			// Shrink the realms button, should be the third button on the list
			final AbstractButtonWidget optionsButton = info.getButtons().get(2);
			optionsButton.setWidth(98);

			// Add a new button
			info.getButtons().add(new SoundButton((screen.width / 2) + 2, ((screen.height / 4) + 96), 72, 20));
			// And another button
			info.getButtons().add(new StopSoundButton(screen, (screen.width / 2) + 80, ((screen.height / 4) + 95), 20, 20));

			// And some automatic validation, make sure the buttons we added are on the list of child elements
			screen.children().stream()
					.filter(element -> element instanceof SoundButton)
					.findAny()
					.orElseThrow(() -> new AssertionError("Failed to find the \"Sound\" button in the screen's elements"));

			screen.children().stream()
					.filter(element -> element instanceof StopSoundButton)
					.findAny()
					.orElseThrow(() -> new AssertionError("Failed to find the \"Stop Sound\" button in the screen's elements"));

			// Register render event to draw an icon on the screen
			info.getAfterRenderEvent().register(this::onRender);
			info.getBeforeKeyPressedEvent().register(this::beforeKeyPress);
			info.getAfterKeyPressedEvent().register(this::afterKeyPress);
		}

		// Say something when the screen is resized
		LOGGER.info("Registered resize event");
		info.getAfterResizeEvent().register(this::onResizeScreen);
	}

	private void afterKeyPress(MinecraftClient client, Screen screen, FabricScreen fabricScreen, int key, int scancode, int modifiers) {
		LOGGER.info("After Pressed, Code: {}, Scancode: {}, Modifiers: {}", key, scancode, modifiers);
	}

	private boolean beforeKeyPress(MinecraftClient client, Screen screen, FabricScreen fabricScreen, int key, int scancode, int modifiers) {
		LOGGER.warn("Pressed, Code: {}, Scancode: {}, Modifiers: {}", key, scancode, modifiers);
		return false; // Let actions continue
	}

	private void onResizeScreen(MinecraftClient client, Screen screen, FabricScreen info) {
		if (PRINT_RESIZE_SCREEN_EVENTS) {
			LOGGER.info("Resized screen {} to {}, {}", screen.getClass().getName(), screen.width, screen.height);
		}
	}

	private void onRender(MinecraftClient client, MatrixStack matrices, Screen screen, FabricScreen info, int mouseX, int mouseY, float tickDelta) {
		if (screen instanceof TitleScreen) {
			RenderSystem.pushMatrix();
			// Render an armor icon to test
			client.getTextureManager().bindTexture(InGameHud.GUI_ICONS_TEXTURE);
			DrawableHelper.drawTexture(matrices, (screen.width / 2) - 124, (screen.height / 4) + 96, 20, 20, 34, 9, 9, 9, 256, 256);
			RenderSystem.popMatrix();
		}
	}
}
