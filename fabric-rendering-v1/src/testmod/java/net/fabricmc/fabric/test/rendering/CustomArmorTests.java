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

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class CustomArmorTests implements ModInitializer {
	public static Item customModeledArmor;
	public static Item customTexturedArmor;
	public static Item simpleTexturedArmor;

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("fabric-rendering-v1-testmod:custom_modeled_armor"),
				customModeledArmor = new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT)));

		Registry.register(Registry.ITEM, new Identifier("fabric-rendering-v1-testmod:custom_textured_armor"),
				customTexturedArmor = new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT)));

		Registry.register(Registry.ITEM, new Identifier("fabric-rendering-v1-testmod:simple_textured_armor"),
				simpleTexturedArmor = new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT)));
	}
}
