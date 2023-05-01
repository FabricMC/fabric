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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.client.screen.v1.Screens;

class StopSoundButton extends PressableWidget {
	private final Screen screen;

	StopSoundButton(Screen screen, int x, int y, int width, int height) {
		super(x, y, width, height, Text.of(""));
		this.screen = screen;
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
		// Render the armor icon to test
		drawContext.drawTexture(ScreenTests.GUI_ICONS_TEXTURE, this.getX(), this.getY(), this.width, this.height, 43, 27, 9, 9, 256, 256);

		if (this.isMouseOver(mouseX, mouseY)) {
			drawContext.drawTooltip(Screens.getTextRenderer(this.screen), Text.literal("Click to stop all sounds"), this.getX(), this.getY());
		}
	}

	@Override
	public void onPress() {
		MinecraftClient.getInstance().getSoundManager().stopAll();
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder narrationMessageBuilder) {
	}
}
