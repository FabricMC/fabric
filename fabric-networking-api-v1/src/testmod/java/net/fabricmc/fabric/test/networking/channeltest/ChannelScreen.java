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

package net.fabricmc.fabric.test.networking.channeltest;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

final class ChannelScreen extends Screen {
	private ButtonWidget clientButton;
	private ButtonWidget serverButton;

	ChannelScreen() {
		super(new LiteralText("TODO"));
	}

	@Override
	protected void init() {
		this.clientButton = new ButtonWidget(this.width / 2 - 30, 20, 50, 20, new LiteralText("Client"), this::toClient);
		this.serverButton = new ButtonWidget(this.width / 2 + 30, 20, 50, 20, new LiteralText("Server"), this::toServer);
		this.serverButton.active = false; // Server is inactive by default
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
	}

	private void toServer(ButtonWidget button) {
		this.clientButton.active = true;
		button.active = false;
	}

	private void toClient(ButtonWidget button) {
		this.serverButton.active = true;
		button.active = false;
	}
}
