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

package net.fabricmc.fabric.impl.itemgroup;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

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
		CreativeInventoryScreen gui;
		Type type;

		public ItemGroupButtonWidget(int x, int y, Type type, CreativeGuiExtensions extensions) {
			super(x, y, 11, 11, type.text, (bw) -> type.clickConsumer.accept(extensions));
			this.extensions = extensions;
			this.type = type;
			this.gui = (CreativeInventoryScreen) extensions;
		}

		@Override
		public void render(int mouseX, int mouseY, float float_1) {
			// Copied From Vanilla
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			this.visible = extensions.fabric_isButtonVisible(type);
			this.active = extensions.fabric_isButtonEnabled(type);

			if (this.visible) {
				MinecraftClient minecraftClient = MinecraftClient.getInstance();
				TextRenderer textRenderer = minecraftClient.textRenderer;
				minecraftClient.getTextureManager().bindTexture(BUTTON_TEX);
				GlStateManager.disableLighting();
				GlStateManager.color4f(1F, 1F, 1F, 1F);
				this.blit(this.x, this.y, 0, (active ? (isHovered() ? 24 : 0) : 12), 11, 11);
				int textColor = 0xffe0e0e0;
				if (!this.active) {
					textColor = 0xffa0a0a0;
				} else if (this.isHovered()) {
					textColor = 0xffffffa0;
				}
				// Manually pre-mirrors the arrow because the text renderer will mirror it back if it is right to left
				this.drawCenteredString(textRenderer, textRenderer.isRightToLeft() ? textRenderer.mirror(getMessage()) : getMessage(), this.x + this.width / 2, this.y + (this.height - 6) / 2, textColor | MathHelper.ceil(this.alpha * 255.0F) << 24);
				if (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
					gui.renderTooltip(I18n.translate("fabric.gui.creativeTabPage", extensions.fabric_currentPage() + 1, ((ItemGroup.GROUPS.length - 12) / 9) + 2), mouseX, mouseY);
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
