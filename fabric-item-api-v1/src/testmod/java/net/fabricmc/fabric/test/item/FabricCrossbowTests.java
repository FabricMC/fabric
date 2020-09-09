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

import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricCrossbowHooks;

public class FabricCrossbowTests implements ModInitializer {
	@Override
	public void onInitialize() {
		// Registers an item with a custom equipment slot.
		Item testItem = new TestCrossbow(new Item.Settings().group(ItemGroup.COMBAT));
		Registry.register(Registry.ITEM, new Identifier("fabric-item-api-v1-testmod", "test_crossbow"), testItem);
	}

	public static class TestCrossbow extends CrossbowItem implements FabricCrossbowHooks {
		public TestCrossbow(Settings settings) {
			super(settings);
		}

		@Override
		public void createArrow(ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
			persistentProjectileEntity.setDamage(1000);
		}

		@Override
		public float getSpeed() {
			return 1.6f;
		}
	}
}
