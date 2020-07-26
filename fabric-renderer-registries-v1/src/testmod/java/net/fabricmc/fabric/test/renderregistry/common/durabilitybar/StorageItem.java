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

package net.fabricmc.fabric.test.renderregistry.common.durabilitybar;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * An example item for something that stores units of something (i.e. energy / mana)
 * with a capacity of 1000 units. Right-clicking with the item in the air will cycle the level.
 */
public class StorageItem extends Item {
	private static final int CAPACITY = 1000;

	public StorageItem(Settings settings) {
		super(settings);
	}

	public float getFillLevel(ItemStack stack) {
		CompoundTag tag = stack.getTag();

		if (tag == null) {
			return 0;
		}

		return (CAPACITY - tag.getInt("current")) / (float) CAPACITY;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack item = user.getStackInHand(hand).copy();

		CompoundTag tag = item.getOrCreateTag();
		int current = tag.getInt("current");

		if (current >= CAPACITY) {
			current = 0; // cycle back to 0
		} else {
			current = Math.min(CAPACITY, current + CAPACITY / 5);
		}

		tag.putInt("current", current);

		return TypedActionResult.success(item);
	}
}
