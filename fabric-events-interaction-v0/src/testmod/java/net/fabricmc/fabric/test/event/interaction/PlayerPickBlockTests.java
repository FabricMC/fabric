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

package net.fabricmc.fabric.test.event.interaction;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;

public class PlayerPickBlockTests implements ModInitializer {
	@Override
	public void onInitialize() {
		ClientPickBlockApplyCallback.EVENT.register((player, result, stack) -> {
			if (player.getStackInHand(Hand.MAIN_HAND).getItem() == Items.DIAMOND) {
				return new ItemStack(Items.OBSIDIAN);
			}

			if (stack.getItem() == Items.GRASS_BLOCK) {
				return ItemStack.EMPTY;
			}

			return stack;
		});
	}
}
