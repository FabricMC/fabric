/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.client.itemgroup;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ingame.CreativePlayerInventoryGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class FabricCreativeGuiComponents {

	private static final Identifier BUTTON_TEX = new Identifier("fabric", "textures/gui/creative_buttons.png");
	public static final Set<ItemGroup> COMMON_GROUPS = new HashSet<>();

	static {
		COMMON_GROUPS.add(ItemGroup.SEARCH);
		COMMON_GROUPS.add(ItemGroup.INVENTORY);
		COMMON_GROUPS.add(ItemGroup.HOTBAR);
	}

	public static class ItemGroupButtonWidget extends ButtonWidget {

		CreativeGuiExtensions extensions;
		CreativePlayerInventoryGui gui;
		Type type;

		public ItemGroupButtonWidget(int id, int x, int y, Type type, CreativeGuiExtensions extensions) {
			super(id, x, y, 10, 11, type.text);
			this.extensions = extensions;
			this.type = type;
			this.gui = (CreativePlayerInventoryGui) extensions;
		}

		@Override
		public void onPressed(double double_1, double double_2) {
			super.onPressed(double_1, double_2);
			type.clickConsumer.accept(extensions);
		}

		@Override
		public void draw(int mouseX, int mouseY, float float_1) {
			this.visible = extensions.fabric_isButtonVisible(type);
			this.enabled = extensions.fabric_isButtonEnabled(type);

			if (this.visible) {
				MinecraftClient minecraftClient = MinecraftClient.getInstance();
				minecraftClient.getTextureManager().bindTexture(BUTTON_TEX);
				GlStateManager.disableLighting();
				GlStateManager.color4f(1F, 1F, 1F, 1F);
				this.drawTexturedRect(this.x, this.y, (type == Type.NEXT ? 12 : 0), (enabled ? 0 : 12), 12, 12);

				if(mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height){
					gui.drawTooltip(I18n.translate("fabric.gui.creativeTabPage", extensions.fabric_currentPage() + 1, ((ItemGroup.GROUPS.length - 12) / 9) + 2), mouseX, mouseY);
				}
			}
		}
	}

	public enum Type {

		NEXT(">", CreativeGuiExtensions::fabric_nextPage),
		PREVIOUS("<", CreativeGuiExtensions::fabric_previousPage);

		String text;
		Consumer<CreativeGuiExtensions> clickConsumer;

		Type(String text, Consumer<CreativeGuiExtensions> clickConsumer) {
			this.text = text;
			this.clickConsumer = clickConsumer;
		}
	}

}
