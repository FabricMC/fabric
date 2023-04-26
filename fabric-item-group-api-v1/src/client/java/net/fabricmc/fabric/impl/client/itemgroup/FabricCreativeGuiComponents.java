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

package net.fabricmc.fabric.impl.client.itemgroup;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FabricCreativeGuiComponents {
	private static final Identifier BUTTON_TEX = new Identifier("fabric", "textures/gui/creative_buttons.png");
	public static final Set<ItemGroup> COMMON_GROUPS = Set.of(ItemGroups.SEARCH, ItemGroups.INVENTORY, ItemGroups.HOTBAR).stream()
			.map(Registries.ITEM_GROUP::getOrThrow)
			.collect(Collectors.toSet());

	public static class ItemGroupButtonWidget extends ButtonWidget {
		final CreativeGuiExtensions extensions;
		final CreativeInventoryScreen gui;
		final Type type;

		public ItemGroupButtonWidget(int x, int y, Type type, CreativeGuiExtensions extensions) {
			super(x, y, 11, 12, type.text, (bw) -> type.clickConsumer.accept(extensions), ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
			this.extensions = extensions;
			this.type = type;
			this.gui = (CreativeInventoryScreen) extensions;
		}

		@Override
		public void render(DrawContext drawContext, int mouseX, int mouseY, float float_1) {
			this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
			this.visible = extensions.fabric_isButtonVisible(type);
			this.active = extensions.fabric_isButtonEnabled(type);

			if (this.visible) {
				int u = active && this.isHovered() ? 22 : 0;
				int v = active ? 0 : 12;

				drawContext.drawTexture(BUTTON_TEX, this.getX(), this.getY(), u + (type == Type.NEXT ? 11 : 0), v, 11, 12);

				if (this.hovered) {
					int pageCount = (int) Math.ceil((ItemGroups.getGroupsToDisplay().size() - COMMON_GROUPS.size()) / 9D);
					drawContext.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable("fabric.gui.creativeTabPage", extensions.fabric_currentPage() + 1, pageCount), mouseX, mouseY);
				}
			}
		}
	}

	public enum Type {
		NEXT(Text.literal(">"), CreativeGuiExtensions::fabric_nextPage),
		PREVIOUS(Text.literal("<"), CreativeGuiExtensions::fabric_previousPage);

		final Text text;
		final Consumer<CreativeGuiExtensions> clickConsumer;

		Type(Text text, Consumer<CreativeGuiExtensions> clickConsumer) {
			this.text = text;
			this.clickConsumer = clickConsumer;
		}
	}
}
