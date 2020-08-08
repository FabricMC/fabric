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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.ShieldRegistry;

public class ShieldTest implements ModInitializer {
	public static final Item SHIELD = new Item(new Item.Settings().maxDamage(200)) {
		// These are the same as ShieldItem

		@Override
		public UseAction getUseAction(ItemStack stack) {
			return UseAction.BLOCK;
		}

		@Override
		public int getMaxUseTime(ItemStack stack) {
			return 72000;
		}

		@Override
		public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
			ItemStack itemstack = player.getStackInHand(hand);
			player.setCurrentHand(hand);
			return TypedActionResult.consume(itemstack);
		}
	};

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("fabric", "shield"), SHIELD);
		ShieldRegistry.add(SHIELD, 50);
	}
}
