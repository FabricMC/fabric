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

package net.fabricmc.fabric.test.base;

import java.time.Duration;
import java.time.LocalDateTime;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.test.base.mixin.ScreenAccessor;
import net.fabricmc.loader.api.FabricLoader;

public class FabricApiAutoTestClient implements ClientModInitializer {
	int ticks = 0;
	boolean loaded = false;

	@Override
	public void onInitializeClient() {
		if (System.getProperty("fabric.autoTest") == null) {
			return;
		}

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!loaded && screen instanceof TitleScreen) {
				after1s(screen, () -> clickButton(screen, "menu.singleplayer"));
			}

			if (screen instanceof SelectWorldScreen) {
				after1s(screen, () -> clickButton(screen, "selectWorld.create"));
			}

			if (screen instanceof CreateWorldScreen) {
				after1s(screen, () -> clickButton(screen, "selectWorld.create"));
			}

			if (screen instanceof ConfirmScreen) {
				after1s(screen, () -> clickButton(screen, "gui.yes"));
			}

			if (screen instanceof GameMenuScreen) {
				after1s(screen, () -> clickButton(screen, "menu.returnToMenu"));
			}

			// See server tick event bellow

			if (loaded && screen instanceof TitleScreen) {
				after1s(screen, () -> clickButton(screen, "menu.quit"));
			}
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ticks++;

			if (!loaded && ticks > 200) {
				loaded = true;
				MinecraftClient.getInstance().submit(() -> MinecraftClient.getInstance().setScreen(new GameMenuScreen(true)));
			}
		});
	}

	private void after1s(Screen screen, Runnable runnable) {
		ScreenEvents.afterTick(screen).register(new ScreenEvents.AfterTick() {
			LocalDateTime time = LocalDateTime.now();

			@Override
			public void afterTick(Screen screen) {
				if (MinecraftClient.getInstance().getOverlay() != null) {
					// The main menu is hidden behind the loading overlay, wait for it to go.
					time = LocalDateTime.now();
					return;
				}

				if (LocalDateTime.now().isAfter(time.plus(Duration.ofSeconds(1)))) {
					runnable.run();
				}
			}
		});
	}

	private void clickButton(Screen screen, String translationKey) {
		final String expected = Text.translatable(translationKey).getString();
		final ScreenAccessor screenAccessor = (ScreenAccessor) screen;

		ScreenshotRecorder.saveScreenshot(FabricLoader.getInstance().getGameDir().toFile(), translationKey + ".png", MinecraftClient.getInstance().getFramebuffer(), (message) -> {
		});

		for (Drawable drawable : screenAccessor.getDrawables()) {
			if (drawable instanceof ButtonWidget buttonWidget) {
				if (expected.equals(buttonWidget.getMessage().getString())) {
					buttonWidget.onPress();
					break;
				}
			}

			if (drawable instanceof GridWidget gridWidget) {
				for (Element child : gridWidget.children()) {
					if (child instanceof ButtonWidget buttonWidget) {
						if (expected.equals(buttonWidget.getMessage().getString())) {
							buttonWidget.onPress();
							break;
						}
					}
				}
			}
		}
	}
}
