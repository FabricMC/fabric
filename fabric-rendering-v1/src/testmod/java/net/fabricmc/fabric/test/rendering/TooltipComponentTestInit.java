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

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import net.fabricmc.api.ModInitializer;

public class TooltipComponentTestInit implements ModInitializer {
	public static Item CUSTOM_TOOLTIP_ITEM = new CustomTooltipItem();
	public static RegistryEntry<ArmorMaterial> TEST_ARMOR_MATERIAL = Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of("fabric-rendering-v1-testmod", "test_material"), createTestArmorMaterial());
	public static Item CUSTOM_ARMOR_ITEM = new ArmorItem(TEST_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings());

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, Identifier.of("fabric-rendering-v1-testmod", "custom_tooltip"), CUSTOM_TOOLTIP_ITEM);
		Registry.register(Registries.ITEM, Identifier.of("fabric-rendering-v1-testmod", "test_chest"), CUSTOM_ARMOR_ITEM);
	}

	private static class CustomTooltipItem extends Item {
		CustomTooltipItem() {
			super(new Settings());
		}

		@Override
		public Optional<TooltipData> getTooltipData(ItemStack stack) {
			return Optional.of(new Data(stack.getTranslationKey()));
		}
	}

	public record Data(String string) implements TooltipData {
	}

	private static ArmorMaterial createTestArmorMaterial() {
		return new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
			map.put(ArmorItem.Type.BOOTS, 1);
			map.put(ArmorItem.Type.LEGGINGS, 2);
			map.put(ArmorItem.Type.CHESTPLATE, 3);
			map.put(ArmorItem.Type.HELMET, 1);
			map.put(ArmorItem.Type.BODY, 3);
		}),
			0,
			SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
				() -> Ingredient.ofItems(Items.LEATHER),
			List.of(new ArmorMaterial.Layer(Identifier.of("fabric-rendering-v1-testmod", "test_material"))),
			0,
			0
		);
	}
}
