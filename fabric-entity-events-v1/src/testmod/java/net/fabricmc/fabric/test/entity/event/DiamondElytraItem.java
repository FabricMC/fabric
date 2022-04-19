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

package net.fabricmc.fabric.test.entity.event;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemGroup;

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;

public class DiamondElytraItem extends ArmorItem implements FabricElytraItem {
	public DiamondElytraItem() {
		super(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Settings().maxCount(1).group(ItemGroup.COMBAT));
	}
}
