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

package net.fabricmc.fabric.test.renderer.registry.client.countlabel;

import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemLabelInfo;

public class ObfuscatedItemLabelInfo implements ItemLabelInfo {
	@Override
	public boolean isVisible(ItemStack stack, String override) {
		return true;
	}

	@Override
	public Text getContents(ItemStack stack, String override) {
		return new LiteralText(override == null ? Integer.toString(stack.getCount()) : override)
				.styled(style -> style.withFormatting(Formatting.OBFUSCATED));
	}

	@Override
	public int getColor(ItemStack stack, String override) {
		return 0xFFFFFF;
	}
}
