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

package net.fabricmc.fabric.test.networking.channels;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
class ChannelScreen extends Screen {
	private final ChannelRegistrationClientTest.ServerState serverState;
	private ChannelList channelList;

	ChannelScreen(ChannelRegistrationClientTest.ServerState serverState) {
		super(new LiteralText("Channel testmod screen"));
		this.serverState = serverState;
	}

	@Override
	protected void init() {
		this.channelList = new ChannelList(this.client, this.width - 40, this.height - 50, 20, this.height - 30, 10);

		for (Identifier channel : this.serverState.getSupportedChannels()) {
			this.channelList.addEntry(new ChannelList.Entry(this.channelList, this.textRenderer, channel));
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		this.channelList.render(matrices, mouseX, mouseY, delta);
	}
}
