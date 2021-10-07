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

package net.fabricmc.fabric.test.rendering;

import java.util.Optional;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class TooltipComponentTestInit implements ModInitializer {
	public static Item CUSTOM_TOOLTIP_ITEM = new CustomTooltipItem();

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("fabric-rendering-v1-testmod", "custom_tooltip"), CUSTOM_TOOLTIP_ITEM);
	}

	private static class CustomTooltipItem extends Item {
		CustomTooltipItem() {
			super(new Settings().group(ItemGroup.MISC));
		}

		@Override
		public Optional<TooltipData> getTooltipData(ItemStack stack) {
			return Optional.of(new Data(stack.getTranslationKey()));
		}
	}

	public record Data(String string) implements TooltipData { }
}
