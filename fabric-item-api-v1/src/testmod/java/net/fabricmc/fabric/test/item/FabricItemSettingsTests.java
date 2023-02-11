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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public class FabricItemSettingsTests implements ModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(FabricItemSettingsTests.class);

	@Override
	public void onInitialize() {
		EquipmentSlotProvider equipmentSlotProvider = stack -> EquipmentSlot.CHEST;
		CustomDamageHandler damageHandler = (stack, amount, entity, breakCallback) -> amount;

		// Registers an item with a custom equipment slot and a no-op custom damage handler.
		Item testItem = new Item(new FabricItemSettings().equipmentSlot(equipmentSlotProvider).customDamage(damageHandler));
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "test_item"), testItem);

		// Test getters
		assertIdentical(EquipmentSlotProvider.get(testItem), equipmentSlotProvider, "equipment slot provider");
		assertIdentical(EquipmentSlotProvider.get(Items.APPLE), null, "equipment slot provider");
		assertIdentical(CustomDamageHandler.get(testItem), damageHandler, "damage handler");
		assertIdentical(CustomDamageHandler.get(Items.APPLE), null, "damage handler");
		LOGGER.info("FabricItemSettings tests passed!");
	}

	private static void assertIdentical(Object found, Object expected, String message) {
		if (found != expected) {
			throw new AssertionError("Expected " + expected + " for " + message + ", but was " + found);
		}
	}
}
