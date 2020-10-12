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

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings.AttackSound;

public class FabricItemSettingsTests implements ModInitializer {
	@Override
	public void onInitialize() {
		// Registers an item with a custom equipment slot.
		SoundPlayer sound = (world,player)->world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, player.getSoundCategory(), 1.0F, 1.0F);
		Item testItem = new Item(new FabricItemSettings()
			.group(ItemGroup.MISC)
			.equipmentSlot(stack -> EquipmentSlot.CHEST)
			.soundEvent(AttackSound.CRITIAL, sound)
			.soundEvent(AttackSound.KNOCKBACK, sound)
			.soundEvent(AttackSound.NO_DAMAGE, sound)
			.soundEvent(AttackSound.STRONG, sound)
			.soundEvent(AttackSound.SWEEPING, sound)
			.soundEvent(AttackSound.WEAK, sound)
			);
		Registry.register(Registry.ITEM, new Identifier("fabric-item-api-v1-testmod", "test_item"), testItem);
	}
}
