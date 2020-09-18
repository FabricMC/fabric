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

package net.fabricmc.fabric.test.extensibility;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.extensibility.item.v1.FabricBowHooks;

public class FabricBowTests implements ModInitializer {
	@Override
	public void onInitialize() {
		// Registers a custom bow.
		Item testItem = new TestBow(new Item.Settings().group(ItemGroup.COMBAT));
		Registry.register(Registry.ITEM, new Identifier("fabric-extensibility-api-v1-testmod", "test_bow"), testItem);
	}

	public static class TestBow extends BowItem implements FabricBowHooks {
		public TestBow(Settings settings) {
			super(settings);
		}

		@Override
		public void onBowRelease(ItemStack arrowStack, LivingEntity user, int remainingUseTicks, PersistentProjectileEntity persistentProjectileEntity) {
			persistentProjectileEntity.setPunch(100);
		}
	}
}
