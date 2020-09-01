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
import net.fabricmc.fabric.api.client.screen.v1.ScreenExtensions;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class ScreenTests implements ClientModInitializer {
	public static final Random RANDOM = new Random();
	private static final Logger LOGGER = LogManager.getLogger("FabricScreenApiTests");
	private static boolean PRINT_RESIZE_SCREEN_EVENTS = System.getProperty("fabric-screen-api-testmod.printResizeScreenEvents") != null;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Started Screen Testmod");
		ScreenEvents.BEFORE_INIT.register(this::beforeInitScreen);
		ScreenEvents.AFTER_INIT.register(this::afterInitScreen);
	}

	private void beforeInitScreen(MinecraftClient client, Screen screen, ScreenExtensions info, int windowWidth, int windowHeight) {
		// TODO: Write tests listening to addition of child elements
	}

	private void afterInitScreen(MinecraftClient client, Screen screen, ScreenExtensions info, int windowWidth, int windowHeight) {
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
			info.getKeyboardEvents().getBeforeKeyPressedEvent().register(this::beforeKeyPress);
			info.getKeyboardEvents().getAfterKeyPressedEvent().register(this::afterKeyPress);
		}
	}

	private void afterKeyPress(MinecraftClient client, Screen screen, ScreenExtensions context, int key, int scancode, int modifiers) {
		LOGGER.info("After Pressed, Code: {}, Scancode: {}, Modifiers: {}", key, scancode, modifiers);
	}

	private boolean beforeKeyPress(MinecraftClient client, Screen screen, ScreenExtensions context, int key, int scancode, int modifiers) {
		LOGGER.warn("Pressed, Code: {}, Scancode: {}, Modifiers: {}", key, scancode, modifiers);
		return false; // Let actions continue
	}

	private void onRender(MinecraftClient client, MatrixStack matrices, Screen screen, ScreenExtensions info, int mouseX, int mouseY, float tickDelta) {
		if (screen instanceof TitleScreen) {
			RenderSystem.pushMatrix();
			// Render an armor icon to test
			client.getTextureManager().bindTexture(InGameHud.GUI_ICONS_TEXTURE);
			DrawableHelper.drawTexture(matrices, (screen.width / 2) - 124, (screen.height / 4) + 96, 20, 20, 34, 9, 9, 9, 256, 256);
			RenderSystem.popMatrix();
		}
	}
}
