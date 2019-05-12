/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.events;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class PickBlockEventModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPickBlockGatherCallback.EVENT.register((player, result) -> {
			if (result instanceof BlockHitResult) {
				BlockView view = player.getEntityWorld();
				BlockPos pos = ((BlockHitResult) result).getBlockPos();
				BlockState state = view.getBlockState(pos);

				if (state.getBlock() == Blocks.STONE) {
					return new ItemStack(Blocks.OAK_WOOD);
				}
			}

			return ItemStack.EMPTY;
		});

		ClientPickBlockApplyCallback.EVENT.register((player, result, stack) -> {
			if (stack.getItem() == Item.getItemFromBlock(Blocks.OAK_WOOD)) {
				return new ItemStack(Blocks.ACACIA_WOOD);
			} else {
				return stack;
			}
		});
	}
}
