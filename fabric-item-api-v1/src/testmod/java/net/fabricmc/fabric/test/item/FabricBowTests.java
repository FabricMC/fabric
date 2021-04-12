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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.ShotProjectileEvents;
import net.fabricmc.fabric.api.item.v1.bow.FabricBowItem;

public class FabricBowTests implements ModInitializer {
	public static final Item TEST_BOW = new FabricBowItem(new Item.Settings().group(ItemGroup.COMBAT)) {
		@Override
		public void onProjectileShot(ItemStack bowStack, ItemStack arrowStack, LivingEntity user, float pullProgress, PersistentProjectileEntity persistentProjectileEntity) {
			persistentProjectileEntity.setPunch(100);
		}
	};

	@Override
	public void onInitialize() {
		// Registers a custom bow.
		Registry.register(Registry.ITEM, new Identifier("fabric-combat-api-v1-testmod", "test_bow"), TEST_BOW);
		ShotProjectileEvents.BOW_MODIFY_SHOT_PROJECTILE.register((ShotProjectileEvents.ModifyProjectileFromBow) TEST_BOW);
	}
}
