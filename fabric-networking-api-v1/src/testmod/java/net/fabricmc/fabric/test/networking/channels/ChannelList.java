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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
class ChannelList extends AlwaysSelectedEntryListWidget<ChannelList.Entry> {
	ChannelList(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		super(client, width, height, top, bottom, itemHeight);
	}

	@Override
	public int addEntry(Entry entry) {
		return super.addEntry(entry);
	}

	@Environment(EnvType.CLIENT)
	static class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
		private final ChannelList list;
		private final TextRenderer textRenderer;
		private final Identifier channel;

		Entry(ChannelList list, TextRenderer textRenderer, Identifier channel) {
			this.list = list;
			this.textRenderer = textRenderer;
			this.channel = channel;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			//noinspection ConstantConditions
			this.textRenderer.draw(matrices, new LiteralText(this.channel.toString()), x, y, Formatting.WHITE.getColorValue());
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			this.list.setFocused(this);
			return true;
		}
	}
}
