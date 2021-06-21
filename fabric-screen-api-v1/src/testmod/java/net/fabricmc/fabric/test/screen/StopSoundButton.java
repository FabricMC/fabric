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

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

class StopSoundButton extends PressableWidget {
	private final Screen screen;

	StopSoundButton(Screen screen, int x, int y, int width, int height) {
		super(x, y, width, height, Text.of(""));
		this.screen = screen;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		// Render the armor icon to test
		RenderSystem.setShaderTexture(0, InGameHud.GUI_ICONS_TEXTURE);
		DrawableHelper.drawTexture(matrices, this.x, this.y, this.width, this.height, 43, 27, 9, 9, 256, 256);

		if (this.isMouseOver(mouseX, mouseY)) {
			this.screen.renderTooltip(matrices, new LiteralText("Click to stop all sounds"), this.x, this.y);
		}
	}

	@Override
	public void onPress() {
		MinecraftClient.getInstance().getSoundManager().stopAll();
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder arg) {
	}
}
