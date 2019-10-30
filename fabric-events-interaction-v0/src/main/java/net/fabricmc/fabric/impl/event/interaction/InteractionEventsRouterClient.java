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

package net.fabricmc.fabric.impl.event.interaction;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.fabricmc.fabric.api.entity.EntityPickInteractionAware;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;

public class InteractionEventsRouterClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPickBlockGatherCallback.EVENT.register(((player, result) -> {
			if (result instanceof BlockHitResult) {
				BlockView view = player.getEntityWorld();
				BlockPos pos = ((BlockHitResult) result).getBlockPos();
				BlockState state = view.getBlockState(pos);

				if (state.getBlock() instanceof BlockPickInteractionAware) {
					return (((BlockPickInteractionAware) state.getBlock()).getPickedStack(state, view, pos, player, result));
				}
			} else if (result instanceof EntityHitResult) {
				Entity entity = ((EntityHitResult) result).getEntity();

				if (entity instanceof EntityPickInteractionAware) {
					return ((EntityPickInteractionAware) entity).getPickedStack(player, result);
				}
			}

			return ItemStack.EMPTY;
		}));
	}
}
