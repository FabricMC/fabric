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

	@Override
	public void onInitializeClient() {
		LOGGER.info("Started Screen Testmod");
		ScreenEvents.AFTER_INIT.register(this::onInit);
		ScreenEvents.AFTER_RENDER.register(this::onRender);
	}

	private void onInit(MinecraftClient client, Screen screen, FabricScreen context, int windowWidth, int windowHeight) {
		LOGGER.info("Initializing {}", screen.getClass().getName());

		if (screen instanceof TitleScreen) {
			// Shrink the realms button, should be the third button on the list
			final AbstractButtonWidget optionsButton = context.getButtons().get(2);
			optionsButton.setWidth(98);

			// Add a new button
			context.getButtons().add(new SoundButton((screen.width / 2) + 2, ((screen.height / 4) + 96), 72, 20));
			// And another button
			context.getButtons().add(new StopSoundButton(screen, (screen.width / 2) + 80, ((screen.height / 4) + 95), 20, 20));

			// And some automatic validation, make sure the buttons we added are on the list of child elements
			screen.children().stream()
					.filter(element -> element instanceof SoundButton)
					.findAny()
					.orElseThrow(() -> new AssertionError("Failed to find the \"Sound\" button in the screen's elements"));

			screen.children().stream()
					.filter(element -> element instanceof StopSoundButton)
					.findAny()
					.orElseThrow(() -> new AssertionError("Failed to find the \"Stop Sound\" button in the screen's elements"));
		}
	}

	private void onRender(MinecraftClient client, MatrixStack matrices, Screen screen, FabricScreen context, int mouseX, int mouseY, float tickDelta) {
		if (screen instanceof TitleScreen) {
			RenderSystem.pushMatrix();
			// Render an armor icon to test
			client.getTextureManager().bindTexture(InGameHud.GUI_ICONS_TEXTURE);
			DrawableHelper.drawTexture(matrices, (screen.width / 2) - 124, (screen.height / 4) + 96, 20, 20, 34, 9, 9, 9, 256, 256);
			RenderSystem.popMatrix();
		}
	}
}
