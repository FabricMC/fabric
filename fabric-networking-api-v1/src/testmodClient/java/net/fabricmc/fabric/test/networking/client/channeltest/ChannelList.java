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

package net.fabricmc.fabric.test.networking.client.channeltest;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

final class ChannelList extends EntryListWidget<ChannelList.Entry> {
	ChannelList(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		super(client, width, height, top, bottom, itemHeight);
	}

	@Override
	public int addEntry(Entry entry) {
		return super.addEntry(entry);
	}

	void clear() {
		this.clearEntries();
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder arg) {
		// TODO seems to be possibly accessibility related
	}

	class Entry extends EntryListWidget.Entry<Entry> {
		private final Identifier channel;

		Entry(Identifier channel) {
			this.channel = channel;
		}

		@Override
		public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			drawContext.drawTooltip(ChannelList.this.client.textRenderer, Text.literal(this.channel.toString()).formatted(Formatting.WHITE), x, y);
		}
	}
}
