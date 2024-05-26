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

package net.fabricmc.fabric.test.item;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import net.fabricmc.api.ModInitializer;

public class ArmorKnockbackResistanceTest implements ModInitializer {
	private static final RegistryEntry<ArmorMaterial> WOOD_ARMOR = Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of("fabric-item-api-v1-testmod", "wood"), createTestArmorMaterial());

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, Identifier.of("fabric-item-api-v1-testmod",
				"wooden_boots"), new ArmorItem(WOOD_ARMOR, ArmorItem.Type.BOOTS, new Item.Settings()));
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
			List.of(new ArmorMaterial.Layer(Identifier.of("fabric-item-api-v1-testmod", "wood"))),
			0,
			0.5F
		);
	}
}
