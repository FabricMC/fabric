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

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemGroup;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class FabricCreativeGuiComponents {

	public static final Set<ItemGroup> COMMON_GROUPS = new HashSet<>();

	static {
		COMMON_GROUPS.add(ItemGroup.SEARCH);
		COMMON_GROUPS.add(ItemGroup.INVENTORY);
		COMMON_GROUPS.add(ItemGroup.HOTBAR);
	}

	public static class ItemGroupButtonWidget extends ButtonWidget {

		CreativeGuiExtensions extensions;
		Type type;

		public ItemGroupButtonWidget(int id, int x, int y, Type type, CreativeGuiExtensions extensions) {
			super(id, x, y, 10, 11, type.text);
			this.extensions = extensions;
			this.type = type;
		}

		@Override
		public void onPressed(double double_1, double double_2) {
			super.onPressed(double_1, double_2);
			type.clickConsumer.accept(extensions);
		}

		@Override
		public void draw(int int_1, int int_2, float float_1) {
			this.visible = extensions.fabric_isButtonVisible(type);
			this.enabled = extensions.fabric_isButtonEnabled(type);

			super.draw(int_1, int_2, float_1);
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
